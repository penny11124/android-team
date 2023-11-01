//SPDX-License-Identifier: UNLICENSED
//0.5.8 used while developing
pragma solidity >=0.4.21;

contract ticket_verifier{

    //idea: convert public key to address, prefix payload with ethereum format, use internal ecrecover to verify signature to address

    bytes owner_id = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEs_4olp8SPvgngcpeE5JM3zp9p0Frpq30pgYgeIv0RuKKA_hBfCBGmIIPJ0bG4SElsZPb-pG10LBWIpRot9il4w==";
    //bytes prefix = "\x19Ethereum Signed Message:\n32";
    bytes signature = "MEUCIQDDHZXz1VdxgDHkVevGoiZkc3_O0uhNXhvsuft4I-2EWAIgUBWU5zbh84nuXcEibLMoc34_kOXxE9ZqnBRJLJ8BwcE=";
    bytes ticket = '{"device_id": "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEs_4olp8SPvgngcpeE5JM3zp9p0Frpq30pgYgeIv0RuKKA_hBfCBGmIIPJ0bG4SElsZPb-pG10LBWIpRot9il4w==", "holder_id": "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjLLDWW0D01Ug8Kms4uA2aBRimnhbJFznDAxwxQUn82daklYrvhGYE4YmY-BEbYI1ZyxGm6NC-erm1PsHlX00sw==", "request_body": "{\"MANAGEMENT-TYPE\": \"NEW-OWNER\"}", "ticket_type": "MANAGEMENT"}';
    
    address owner_addr = address(uint160(bytes20(keccak256(owner_id))));
    bytes32 hash = sha256(ticket);    

    function ecrecovery(bytes32 hash_in, bytes memory sig) pure public returns (address) {
    bytes32 r;
    bytes32 s;
    uint8 v;

    if (sig.length != 65) {
      revert("invalid signature length");
    }

    assembly {
      r := mload(add(sig, 32))
      s := mload(add(sig, 64))
      v := and(mload(add(sig, 65)), 255)
    }

    if (v < 27) {
      v += 27;
    }

    if (v != 27 && v != 28) {
      revert("invalid v value");
    }

    return ecrecover(hash_in, v, r, s);
  }

  function ecverify(bytes32 hash_in, bytes memory sig, address signer) pure public returns (bool) {
    return signer == ecrecovery(hash_in, sig);
  }

    constructor() {
        ecverify(hash, signature, owner_addr);
    }
}

