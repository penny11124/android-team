//SPDX-License-Identifier: UNLICENSED
//0.5.8 used while developing
pragma solidity >=0.4.21;

contract ticket_verifier{

  function splitSignature(bytes memory sig) internal pure returns (uint8 v, bytes32 r, bytes32 s)
  {
    require(sig.length == 65);
    assembly {
        r := mload(add(sig, 32))
        s := mload(add(sig, 64))
        v := byte(0, mload(add(sig, 96)))
    }
    return (v, r, s);
  }
  
  function prefixed(bytes32 hash) internal pure returns (bytes32) {
    return keccak256(abi.encodePacked("\x19Ethereum Signed Message:\n32", hash));
  }

  function recoverSigner(bytes32 messageHash, bytes memory sig) public pure returns (address)
  {
    (uint8 v, bytes32 r, bytes32 s) = splitSignature(sig);
    
    return ecrecover(messageHash , v, r, s);
  }

}
