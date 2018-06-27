# Decentralized Process Modeling and Instance Tracking
Prototype for demonstrating a decentralized process modeling approach where the integrity of process models is secured by the Ethereum blockchain and versions of process models are managed through the Git DVCS.

License: GNU General Public License v3.0
Copyright: (c) 2018 Felix Härer

### Download

A Windows version can be downloaded in the [release section](https://github.com/fhaer/Decentralized-Process-Modeling-and-Instance-Tracking/releases/). Further versions for Linux and macOS will follow shortly.

Please note that this first release is an alpha version for demonstration purposes. 

### Demonstration Setup
When the program is started, a new ethereum wallet can be created, or, a pre-defined one - registered with the program's smart contract - can be chosen. Furthermore, when the Add button on the left hand side is clicked, the dialog to add a remote repository suggests a link to a Git repository. This repository is set up with process models of a supply chain use case.

The smart contract is deployed in the Ethereum testnet at address [0x44838c369f4c7f781bb6b14df3c45f5b4797af0d](https://ropsten.etherscan.io/address/0x44838c369f4c7f781bb6b14df3c45f5b4797af0d).

### This software uses the following open source software
- Apache Commons (Apache License 2.0, http://www.apache.org/licenses/)
- Apache HttpComponents (Apache License 2.0, http://www.apache.org/licenses/)
- camunda-bpmn.js (Apache License 2.0, http://www.apache.org/licenses/)
- Eclipse Rich Client Platform (Eclipse Distribution License v1.0, http://www.eclipse.org/org/documents/edl-v10.php)
- Geth (LGPL 3.0, https://www.gnu.org/licenses/lgpl-3.0.en.html)
- JavaEWAH (Apache License 2.0, http://www.apache.org/licenses/)
- jgit (Eclipse Distribution License v1.0, http://www.eclipse.org/org/documents/edl-v10.php)
- JSch (BSD-style license, http://www.jcraft.com/jsch/LICENSE.txt)
- JZlib (BSD-style license, http://www.jcraft.com/jsch/LICENSE.txt)
- slf4j (MIT License, https://www.slf4j.org/license.html)
- web3j (Apache License 2.0, http://www.apache.org/licenses/)
