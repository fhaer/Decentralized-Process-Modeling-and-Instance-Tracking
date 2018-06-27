package de.fha.dpmi.versioning;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheCheckout;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectInserter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.TreeFormatter;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.merge.Merger;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;

import de.fha.dpmi.modeling.fileio.ModelingFileIO;
import de.fha.dpmi.modeling.fileio.ModelingFileIOException;
import de.fha.dpmi.view.e4.rcp.parts.JavaFXUtil;

/*
 * License: GNU General Public License v3.0, Copyright: (c) 2018 Felix Härer
 *
 * Under this license, copyright and license notices are required to be preserved
 * and included in any partial or full copies of this software.
 */

/**
 * Implements a version graph using the Git DVCS
 */
public class VersionGraphDVCSGit extends VersionGraphDVCSBase {

	/**
	 * Reference to the git client
	 */
	private static Git git;

	private final static String REMOTE_NAME = "origin";

	private final static String LOCAL_BRANCH_NAME = "master";

	private final static String DEFAULT_GIT_NAME = "DPMI";

	private final static String DEFAULT_GIT_EMAIL = "dpmi@dpmi.com";

	private PersonIdent identity;

	private final CredentialsProvider CREDENTIALS;

	protected VersionsSortedListTotalOrdering versionOrdering;

	/**
	 * Initialize a new Version Graph with given name, location and remote.
	 *
	 * @param path
	 *            local directory
	 * @param uri
	 *            remote https git repository URI
	 * @param username
	 *            remote repository user name
	 * @param password
	 *            remote repository password
	 * @throws VersionGraphException
	 */
	public VersionGraphDVCSGit(String name, File path, String uri, String username, String password)
			throws VersionGraphException {
		super(name, uri, path);
		createLocalRepository();
		setRemote();
		versionOrdering = new VersionsSortedListTotalOrdering();
		CREDENTIALS = new UsernamePasswordCredentialsProvider(username, password);
		initializeSharedBranches();
		initializePrivateBranches();
	}

	private void initializeSharedBranches() throws VersionGraphException {
		try {
			List<Branch> branches = listRemoteBranches();
			for (Branch b : branches) {
				addKnownSharedBranch(b);
			}
		} catch (TransportException e) {
			throw new VersionGraphException("Unable to connect to remote repository.", e);
		} catch (GitAPIException e) {
			throw new VersionGraphException("Unable to list remote branches.", e);
		}
	}

	private void initializePrivateBranches() throws VersionGraphException {
		try {
			List<Branch> branches = listPrivateBranches();
			for (Branch b : branches) {
				addKnownPrivateBranch(b);
			}
		} catch (GitAPIException | IOException e) {
			throw new VersionGraphException("Unable to list local branches.", e);
		}
	}

	@Override
	public void addSharedBranch(Branch b, Version v) throws VersionGraphException {
		initRemoteBranch(b, v);
		addKnownSharedBranch(b);
	}

	@Override
	public void addSharedOrphanBranch(Branch b) throws VersionGraphException {
		initRemoteOrphanBranch(b);
		addKnownSharedBranch(b);
	}

	@Override
	public void removeSharedBranch(Branch b) throws VersionGraphException {
		try {
			git.branchDelete().setBranchNames("refs/heads/" + b.getBranchName()).call();
		} catch (GitAPIException e) {
			throw new VersionGraphException(e);
		}
		// delete remote branch
		// RefSpec refSpec = new
		// RefSpec().setSource(null).setDestination("refs/heads/" +
		// b.getBranchName());
		// git.push().setRefSpecs(refSpec).setRemote(REMOTE_NAME).call();
	}

	private void addPrivateBranch(Version v, Branch branch) throws VersionGraphException {
		initLocalBranch(v, branch);
		addKnownPrivateBranch(branch);
	}

	@Override
	public Version checkOutSharedBranch(Branch branch) throws VersionGraphException {
		try {
			log("Check-out of Branch " + branch.getBranchName() + " ...");
			git.reset().setMode(ResetType.HARD).call();
			fetchBranch(branch);
			Ref ref = git.checkout().setCreateBranch(false).setName(branch.getBranchName())
					.setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).
					// setUpstreamMode(SetupUpstreamMode.TRACK).
					setStartPoint(REMOTE_NAME + "/" + branch.getBranchName()).setForce(true).call();
			Version lastVersion = initializeVersionsSortedListTotalOrdering(branch);
			log("Check-out completed: Version " + ref.toString());
			return lastVersion;
		} catch (TransportException e) {
			throw new VersionGraphException("Unable to connect to remove repository", e);
		} catch (GitAPIException | IOException e) {
			throw new VersionGraphException("Unable to check-out remote branch", e);
		}
	}

	@Override
	public Version checkOutVersion(Version version) throws VersionGraphException {
		try {
			log("Check-out of Version " + version + " ...");
			git.reset().setMode(ResetType.HARD).call();
			// check out version and detach head
			git.checkout().setName(version.getRevCommit().getName()).call();
			// alternative: create a branch:
			// git.checkout().setCreateBranch(true).setName(branch.getBranchName()
			// + "-tmp").setStartPoint(version.getRevCommit().getName()).call();
			log("Check-out completed: Version " + version);
			return version;
		} catch (GitAPIException e) {
			throw new VersionGraphException("Unable to check-out selected version", e);
		}
	}

	@Override
	public Version updateSharedBranch(Branch branch) throws VersionGraphException {
		try {
			log("Fetch Branch " + branch.getBranchName() + " ...");
			fetchBranch(branch);
			Version lastVersion = initializeVersionsSortedListTotalOrdering(branch);
			log("Update finished: Latest version is " + lastVersion);
			return lastVersion;
		} catch (TransportException e) {
			throw new VersionGraphException("Unable to connect to remove repository", e);
		} catch (GitAPIException | IOException e) {
			throw new VersionGraphException("Unable to check-out remote branch", e);
		}
	}

	private Version initializeVersionsSortedListTotalOrdering(Branch sharedBranch) throws MissingObjectException,
			IncorrectObjectTypeException, IOException, RevisionSyntaxException, NoHeadException, GitAPIException {
		versionOrdering = new VersionsSortedListTotalOrdering();
		Version lastVersion = readBranchCommits(sharedBranch);
		for (Branch privateBranch : getKnownPrivateBranches())
			readBranchCommits(privateBranch, sharedBranch);
		return lastVersion;
	}

	@Override
	public int getVersionOrderNumber(Version v) {
		if (v == null)
			return 0;
		return versionOrdering.getOrderNumber(v.getRevCommit().getName());
	}

	@Override
	public Version commit(File f, Branch b) throws VersionGraphException {
		String fileString = DIRECTORY.toURI().relativize(f.toURI()).getPath();
		return commit(b, fileString);
	}

	@Override
	public Version commit(Branch branch, String... filePattern) throws VersionGraphException {
		try {
			Version v = executeCommit(branch, filePattern);
			if (isKnownSharedBranch(branch)) {
				// executePush(branch);
				log("Local commit: version " + v + " is ready to be pushed the the server");
			} else {
				log("Commit completed: " + v);
			}
			return v;
		} catch (NoWorkTreeException | GitAPIException | IOException e) {
			throw new VersionGraphException("Unable to commit files using file patterns", e);
		}
	}

	@Override
	public Version branchCommit(Version version, Branch branch, String... filePattern) throws VersionGraphException {
		if (isKnownSharedBranch(branch.getBranchName()))
			throw new VersionGraphException("Branch name equals the one of an existing shared branch");
		addPrivateBranch(version, branch);
		try {
			Version v = executeCommit(branch, filePattern);
			log("Branch commit completed: " + v);
			return v;
		} catch (NoWorkTreeException | GitAPIException | IOException e) {
			throw new VersionGraphException("Unable to commit files using file patterns", e);
		}
	}

	@Override
	public Version mergeCommit(Version version, Branch sharedBranchMergeBase, String... filePattern)
			throws VersionGraphException {
		Version mergeVersion = version;
		Branch branch = version.getBranch();
		if (isKnownSharedBranch(branch)) {
			throw new VersionGraphException("Unable to merge. Please select a version on a private branch.");
		}
		try {
			// commit uncommitted changes
			if (addFilepattern(filePattern)) {
				log("Uncommitted changes detected: executing commit");
				mergeVersion = executeCommit(branch);
			}
			// set head to branch
			mergeVersion = switchToLocalBranch(branch);
			// merge with shared branch
			Version mergeBaseVersion = sharedBranchMergeBase.getLatestVersion();
			String commitMsg = "Merging branch: " + sharedBranchMergeBase.getBranchName();
			MergeResult mergeResult = git.merge().include(mergeBaseVersion.getRevCommit()).setCommit(true)
					.setFastForward(MergeCommand.FastForwardMode.NO_FF).setSquash(false)
					.setStrategy(MergeStrategy.RECURSIVE).setMessage(commitMsg).call();
			log("Merge results for id: " + mergeBaseVersion + ": " + mergeResult);
			if (mergeResult.getMergeStatus().equals(MergeResult.MergeStatus.MERGED)) {
				ObjectId oid = mergeResult.getNewHead();
				RevCommit commit;
				try (RevWalk revWalk = new RevWalk(git.getRepository())) {
					commit = revWalk.parseCommit(oid);
				}
				mergeBaseVersion = new Version(branch, commit);
				branch.addVersion(mergeBaseVersion);
				versionOrdering.addVersionEnd(mergeBaseVersion);

				mergeVersion.addMerge(mergeBaseVersion);
			} else {
				String conflictMsg = "";
				if (mergeResult.getConflicts() != null && mergeResult.getConflicts().entrySet() != null) {
					for (Map.Entry<String, int[][]> entry : mergeResult.getConflicts().entrySet()) {
						conflictMsg += entry.getKey() + System.lineSeparator();
						for (int[] arr : entry.getValue()) {
							conflictMsg += Arrays.toString(arr) + System.lineSeparator();
						}
					}
					JavaFXUtil.infoBox("Merge Conflict",
							"The following conflicts need to be resolved manually before a commit can take place. Please review and possibly change the files in question and perform a commit when the conflict is resolved.",
							conflictMsg);
				} else {
					JavaFXUtil.infoBox("Merge", "Merge not performed",
							"With the version selected version a merge operation cannot be executed. For a merge, a newer version on the shared branch needs to exist.");
				}
			}
		} catch (GitAPIException | RevisionSyntaxException | IOException e) {
			throw new VersionGraphException(e);
		}
		return mergeVersion;
	}

	private Version executeCommit(Branch branch, String... filePattern) throws GitAPIException, NoFilepatternException,
			NoHeadException, NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException,
			WrongRepositoryStateException, AbortedByHookException, VersionGraphException, IOException {
		addFilepattern(filePattern);
		return executeCommit(branch);
	}

	private boolean addFilepattern(String... filePattern) throws NoFilepatternException, GitAPIException {
		for (String pattern : filePattern)
			git.add().addFilepattern(pattern).call();

		Status status = git.status().call();
		return status.hasUncommittedChanges();
	}

	private Version executeCommit(Branch branch) throws GitAPIException, NoFilepatternException, NoHeadException,
			NoMessageException, UnmergedPathsException, ConcurrentRefUpdateException, WrongRepositoryStateException,
			AbortedByHookException, VersionGraphException, IOException {

		Version v = null;
		v = switchBranchWithChanges(branch, v);

		Status status = git.status().call();

		String commitMsg = "";

		commitMsg += "Added files:" + System.lineSeparator();
		for (String file : status.getAdded())
			commitMsg += file + System.lineSeparator();

		commitMsg += "Changed files:" + System.lineSeparator();
		for (String file : status.getChanged())
			commitMsg += file + System.lineSeparator();

		RevCommit revCommit = git.commit().setMessage(commitMsg).call();
		Version newV = new Version(branch, revCommit);
		branch.addVersion(newV);
		if (branch.isSharedBranch())
			versionOrdering.addVersionEnd(newV);
		else
			versionOrdering.addVersionAfter(newV, v);
		log("Commit " + System.lineSeparator() + commitMsg);

		return newV;
	}

	/**
	 * merges all changes into the given branch
	 *
	 * @param branch
	 * @param v
	 * @return checked-out version of given branch
	 * @throws GitAPIException
	 * @throws VersionGraphException
	 * @throws IOException
	 * @throws InvalidRefNameException
	 */
	private Version switchBranchWithChanges(Branch branch, Version v)
			throws GitAPIException, VersionGraphException, IOException, InvalidRefNameException {
		try {
			RevCommit stash = git.stashCreate().setIncludeUntracked(true).call();
			if (stash != null)
				log("Modified files stashed");
			v = switchToLocalBranch(branch);
			if (stash != null) {
				applyStash(v.getRevCommit(), stash);
				log("Stashed files applied");
				// drop last stash
				int nStashes = git.stashList().call().size();
				git.stashDrop().setStashRef(nStashes - 1).call();
			}
		} catch (NoHeadException e) {
			log("No head present");
		}
		return v;
	}

	private Version switchToLocalBranch(Branch branch) throws VersionGraphException {
		try {
			log("Switch to Branch " + branch.getBranchName() + " ...");
			Ref ref = git.checkout().setCreateBranch(false).setName(branch.getBranchName()).call();
			Version lastVersion = branch.getLatestVersion();
			log("Switched to Version " + ref.toString());
			return lastVersion;
		} catch (TransportException e) {
			throw new VersionGraphException("Unable to connect", e);
		} catch (GitAPIException e) {
			throw new VersionGraphException("Unable to check-out branch locally", e);
		}
	}

	private void applyStash(RevCommit headCommit, RevCommit stashCommit) throws IOException {
		Repository repo = git.getRepository();
		Merger merger = MergeStrategy.THEIRS.newMerger(repo);
		merger.merge(headCommit, stashCommit);
		DirCache dc = repo.lockDirCache();
		ObjectId headTree;
		headTree = repo.resolve(Constants.HEAD + "^{tree}");
		DirCacheCheckout dco = new DirCacheCheckout(repo, headTree, dc, merger.getResultTreeId());
		dco.setFailOnConflict(true);
		dco.checkout();
	}

	/**
	 * pushes the given branch to the remote repository
	 *
	 * @param branch
	 * @throws VersionGraphException
	 */
	public void push(Branch branch) throws VersionGraphException {
		if (isKnownSharedBranch(branch)) {
			try {
				git.push().add(branch.getBranchName()).setForce(true).setCredentialsProvider(CREDENTIALS).call();
				log("DVCS Commit and push to remote server completed");
			} catch (GitAPIException e) {
				throw new VersionGraphException(e);
			}
		}
		// if not checked out from tracking branch:
		// git.push().setRemote("origin").add("master").call();
	}

	/**
	 * Returns a string list of all remote branch names
	 *
	 * @return
	 * @throws GitAPIException
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 */
	private List<Branch> listRemoteBranches() throws InvalidRemoteException, TransportException, GitAPIException {
		List<Branch> branches = new ArrayList<>();
		Collection<Ref> refs = Git.lsRemoteRepository().setCredentialsProvider(CREDENTIALS).setHeads(true)
				.setTags(false).setRemote(URI).call();
		for (Ref ref : refs) {
			String name = ref.getName();
			name = name.replaceFirst("^refs/heads/", "");
			if (!name.equals(LOCAL_BRANCH_NAME)) {
				Branch b = new Branch(name, true);
				branches.add(b);
			}
		}
		return branches;
	}

	/**
	 * Returns a string list of all private branches which do not track a remote
	 * branch.
	 *
	 * @throws GitAPIException
	 * @throws IOException
	 * @throws VersionGraphException
	 */
	private List<Branch> listPrivateBranches() throws GitAPIException, IOException {
		List<Branch> branches = new ArrayList<>();
		List<Ref> refs = git.branchList().call();
		for (Ref ref : refs) {
			boolean branchIsShared = false;
			String branchName = ref.getName();
			branchName = branchName.replaceFirst("^refs/heads/", "");
			for (Branch sharedBranch : getKnownSharedBranches()) {
				if (sharedBranch.getBranchName().equals(branchName))
					branchIsShared = true;
			}
			if (!branchIsShared && !branchName.equals("HEAD") && !branchName.equals(LOCAL_BRANCH_NAME)) {
				Branch b = new Branch(branchName, false);
				branches.add(b);
			}
		}
		return branches;
	}

	/**
	 * fetches given branch from remote
	 *
	 * @param branch
	 * @throws GitAPIException
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 */
	private void fetchBranch(Branch branch) throws GitAPIException, InvalidRemoteException, TransportException {
		RefSpec refSpec = new RefSpec(
				"refs/heads/" + branch.getBranchName() + ":" + "refs/heads/" + branch.getBranchName());
		git.fetch().setRefSpecs(refSpec).setCredentialsProvider(CREDENTIALS).call();
	}

	private void setRemote() throws VersionGraphException {
		try {
			StoredConfig config = git.getRepository().getConfig();
			config.setString("remote", REMOTE_NAME, "url", URI);
			config.save();
		} catch (IOException e) {
			throw new VersionGraphException("Git failed to add the specified remote URI: " + URI, e);
		}
	}

	/**
	 * Initialize a new Version Graph with given name and location.
	 */
	private void createLocalRepository() throws VersionGraphException {
		try {
			// repository = FileRepositoryBuilder.create(new File(directory +
			// File.pathSeparator + ".git"));
			git = Git.init().setDirectory(DIRECTORY).setBare(false).call();
			checkState();
			setGitUserIdentity();
			// rename branch
			// git.branchRename().setNewName("local").call();
			// save reference to last object in graph
			git.getRepository().findRef("HEAD");
		} catch (IllegalStateException | GitAPIException | IOException e) {
			throw new VersionGraphException(
					"Graph initializtaion for shared repoisotry failed. Unable to get head reference to repository, possibly in unclean state.",
					e);
		}
	}

	private void setGitUserIdentity() throws VersionGraphException {
		try {
			StoredConfig config = git.getRepository().getConfig();
			String name = ""; // = config.getString("user", null, "name");
			String email = ""; // = config.getString("user", null, "email");
			if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
				log("Git User identity is unknown!");
				config.setString("user", null, "name", DEFAULT_GIT_NAME);
				config.setString("email", null, "email", DEFAULT_GIT_EMAIL);
				identity = new PersonIdent(DEFAULT_GIT_NAME, DEFAULT_GIT_EMAIL);
				// throw new VersionGraphException("Git user name and email is
				// not configured on this system.");
			} else {
				identity = new PersonIdent(name, email);
				log("Git identity is " + name + " <" + email + ">");
			}
			config.save();
		} catch (IOException e) {
			throw new VersionGraphException("Unable to configure git locally. Failed to set git user.", e);
		}
	}

	/**
	 * Checks if repository is initialized correctly
	 *
	 * @throws VersionGraphException
	 *             throws a VersionGraphException if the state of the repository
	 *             is unknown or is not clean
	 * @throws GitAPIException
	 * @throws NoWorkTreeException
	 * @throws IOException
	 */
	private void checkState() throws VersionGraphException {
		if (git == null) {
			throw new VersionGraphException("Repository not initialized. Git reference not set. ");
		}
		try {
			if (!git.status().call().isClean()) {
				JavaFXUtil.showFailure("Repository State", "Repository initialized in an unclean state.");
				String c1 = "Reset";
				String c2 = "Abort";
				String choice = JavaFXUtil.choiceBox("Repository State", "Reset repository?", c1, c2);
				if (choice.equals(c1))
					reset();
				else
					throw new VersionGraphException("Abort.");
			}
			// if (git.getRepository().resolve(Constants.HEAD) == null) {
			// throw new VersionGraphException("Unable to get head reference to
			// repository.");
			// }
		} catch (NoWorkTreeException e) {
			throw new VersionGraphException("Unbale to determine repository status. Git Exception.", e);
		} catch (GitAPIException e) {
			throw new VersionGraphException("Unbale to determine repository status. Git Exception.", e);
		}
	}

	private void reset() throws CheckoutConflictException, GitAPIException {
		git.clean().setForce(true).setCleanDirectories(true).call();
		git.reset().setMode(ResetType.HARD).call();
		try {
			ModelingFileIO.getInstance().createModelingSystemDirectories(DIRECTORY.toPath());
		} catch (ModelingFileIOException e) {
		}
		// git.reset().setMode(ResetType.HARD).setRef("refs/heads/HEAD~1").call();
	}

	public void resetToCommit(Version v) throws VersionGraphException {
		try {
			git.reset().setMode(ResetType.MIXED).setRef(v.getRevCommit().getName()).call();
			log("Reset to previous commit " + v);
		} catch (GitAPIException e) {
			throw new VersionGraphCredentialException(e);
		}
	}

	/**
	 * Initializes a new remote branch with a tracked local one.
	 *
	 * @param branchName
	 * @param baseVersion
	 * @throws VersionGraphException
	 */
	private void initRemoteBranch(Branch branch, Version baseVersion) throws VersionGraphException {
		try {
			if (refExists(branch.getBranchName()))
				throw new VersionGraphException("Branch not initialized, name not unique: " + branch.getBranchName());
			log("Initializing Branch: " + branch.getBranchName());
			RevCommit startPoint = baseVersion.getRevCommit();
			git.branchCreate().setName(branch.getBranchName()).setForce(true).setStartPoint(startPoint)
					.setUpstreamMode(SetupUpstreamMode.TRACK).call();
			RefSpec refSpec = new RefSpec().setSourceDestination(branch.getBranchName(), branch.getBranchName());
			git.push().setRefSpecs(refSpec).setCredentialsProvider(CREDENTIALS).call();
		} catch (GitAPIException e) {
			throw new VersionGraphException("Unable to create branch.", e);
		}
	}

	/**
	 * Initializes a new remote branch with a tracked local one.
	 *
	 * @param branchName
	 * @throws VersionGraphException
	 */
	private void initRemoteOrphanBranch(Branch branch) throws VersionGraphException {
		try {
			if (refExists(branch.getBranchName()))
				throw new VersionGraphException("Branch not initialized, name not unique: " + branch.getBranchName());
			String message = "Initializing Branch: " + branch.getBranchName();
			log(message);
			Repository repo = git.getRepository();
			ObjectInserter odi = repo.newObjectInserter();
			// String vgraphFile = NAME + "\r\n" + URI + "\r\n" +
			// branch.getBranchName();
			// ObjectId blobId = odi.insert(Constants.OBJ_BLOB,
			// vgraphFile.getBytes(Constants.CHARACTER_ENCODING));
			TreeFormatter tree = new TreeFormatter();
			// tree.append(FileIO.VERSION_GRAPH_FILE_NAME,
			// FileMode.REGULAR_FILE, blobId);
			ObjectId treeId = odi.insert(tree);
			CommitBuilder commit = new CommitBuilder();
			commit.setEncoding(Constants.CHARACTER_ENCODING);
			commit.setMessage(message);
			commit.setTreeId(treeId);
			commit.setAuthor(identity);
			commit.setCommitter(identity);
			ObjectId commitId = odi.insert(commit);
			odi.flush();
			RevWalk revWalk = new RevWalk(repo);
			RevCommit revCommit = revWalk.parseCommit(commitId);
			RefUpdate ru = repo.updateRef("refs/heads/" + branch.getBranchName());
			ru.setNewObjectId(commitId);
			ru.setRefLogMessage("commit: " + revCommit.getShortMessage(), false);
			ru.forceUpdate();
			revWalk.close();
			odi.close();
			RefSpec refSpec = new RefSpec().setSourceDestination(branch.getBranchName(), branch.getBranchName());
			git.push().setRefSpecs(refSpec).setCredentialsProvider(CREDENTIALS).call();
		} catch (GitAPIException | IOException e) {
			throw new VersionGraphException("Unable to create branch.", e);
		}
	}

	/**
	 * Initializes a new local branch.
	 *
	 * @param version
	 * @param branch
	 * @throws VersionGraphException
	 */
	private void initLocalBranch(Version version, Branch branch) throws VersionGraphException {
		try {
			if (refExists(branch.getBranchName()))
				throw new VersionGraphException("Branch not initialized, name not unique: " + branch.getBranchName());
			RevCommit commit = version.getRevCommit();
			log("Initializing Branch: " + branch.getBranchName());
			git.branchCreate().setName(branch.getBranchName()).setStartPoint(commit).setForce(true).call();
			Version vNew = new Version(branch, commit);
			branch.addVersion(vNew);
			version.addBranch(vNew);
		} catch (GitAPIException e) {
			throw new VersionGraphException("Unable to create branch.", e);
		}
	}

	private boolean refExists(String branchName) throws VersionGraphException, GitAPIException {
		List<Ref> refs = git.branchList().call();
		for (Ref ref : refs) {
			if (ref.getName().equals("refs/heads/" + branchName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * parses all commits of the given branch
	 *
	 * @param branch
	 * @return
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws IOException
	 */
	private Version readBranchCommits(Branch branch)
			throws MissingObjectException, IncorrectObjectTypeException, IOException {
		branch.clearVersions();
		Repository repository = git.getRepository();
		Version lastVersion = null;
		try (RevWalk revWalk = new RevWalk(repository)) {
			ObjectId commitId = repository.resolve("refs/heads/" + branch.getBranchName());
			revWalk.markStart(revWalk.parseCommit(commitId));
			// revWalk.markUninteresting(...);
			revWalk.sort(RevSort.REVERSE);
			for (RevCommit revCommit : revWalk) {
				Version v = new Version(branch, revCommit);
				branch.addVersion(v);
				lastVersion = v;
			}
		}
		versionOrdering.addBranchVersions(branch);
		Version firstVersion = branch.getFirstVersion();
		if (firstVersion != null) {
			firstVersion.setIsAgreementProcedureCompleted(true);
			firstVersion.setIsAgreed(true);
		}
		return lastVersion;
	}

	/**
	 * parses all commits of the given branchA, and excludes all commits of the
	 * given branchB
	 *
	 * @param branchA
	 * @param branchB
	 * @return
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws IOException
	 * @throws GitAPIException
	 * @throws NoHeadException
	 * @throws RevisionSyntaxException
	 */
	private Version readBranchCommits(Branch branchA, Branch branchB) throws MissingObjectException,
			IncorrectObjectTypeException, IOException, RevisionSyntaxException, NoHeadException, GitAPIException {
		branchA.clearVersions();
		Repository repository = git.getRepository();
		Version lastVersion = null;
		Iterable<RevCommit> branchCommits = git.log()
				.addRange(repository.resolve("refs/heads/" + branchB.getBranchName()),
						repository.resolve("refs/heads/" + branchA.getBranchName()))
				.call();
		List<RevCommit> branchCommitList = new ArrayList<>();
		branchCommits.forEach(branchCommitList::add);
		Version branchOffVersion = null;
		int nBranchCommits = branchCommitList.size();
		if (nBranchCommits > 0) {
			RevCommit firstCommitOfBranch = branchCommitList.get(nBranchCommits - 1);
			for (RevCommit parent : firstCommitOfBranch.getParents()) {
				branchOffVersion = branchB.getVersion(parent);
			}
		}

		RevWalk walk = new RevWalk(git.getRepository());
		if (branchOffVersion != null) {
			for (int i = branchCommitList.size() - 1; i >= 0; i--) {
				RevCommit targetCommit = walk.parseCommit(branchCommitList.get(i));
				for (Map.Entry<String, Ref> e : git.getRepository().getAllRefs().entrySet()) {
					if (e.getKey().startsWith(Constants.R_HEADS)) {
						if (walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
							String foundInBranch = e.getValue().getName();
							if (branchA.getBranchName().equals(foundInBranch)) {
								break;
							}
						}
					}
				}

				RevCommit revCommit = branchCommitList.get(i);
				Version v = new Version(branchA, revCommit);
				branchA.addVersion(v);
				if (i == nBranchCommits - 1) {
					// first version of branch
					branchOffVersion.addBranch(v);
					versionOrdering.addVersionParallelTo(v, branchOffVersion);
				} else if (revCommit.getParentCount() > 1) {
					for (RevCommit parentRevCommit : revCommit.getParents()) {
						if (!parentRevCommit.equals(lastVersion.getRevCommit())) {
							Version versionMergedFrom = branchB.getVersion(parentRevCommit);
							v.addMerge(versionMergedFrom);
							versionOrdering.addVersionParallelTo(v, versionMergedFrom);
						}
					}
				} else {
					versionOrdering.addVersion(v);
				}
				lastVersion = v;
			}
		}
		walk.close();

		return lastVersion;
	}

	@Override
	public List<String> getVersionedFiles(Version v) throws VersionGraphException {
		List<String> files = new ArrayList<>();

		RevWalk walk = new RevWalk(git.getRepository());
		TreeWalk treeWalk = new TreeWalk(git.getRepository());
		try {
			RevCommit commit = walk.parseCommit(v.getRevCommit());
			RevTree tree = commit.getTree();

			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);
			while (treeWalk.next()) {
				files.add(treeWalk.getPathString());
			}
		} catch (IOException e) {
			throw new VersionGraphException(e);
		} finally {
			treeWalk.close();
			walk.close();
		}
		log("Versioned files: " + Arrays.toString(files.toArray()));
		return files;
	}

}
