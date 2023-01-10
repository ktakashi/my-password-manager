Initial Sequence
================

We've designed the initial components, now we need to design
the operation sequence. The first step is always easy, a user
get authenticated. Once the user is authenticated, then the
user needs to get a transport key. A transport key is a secret
key which needs to be shared by both the application and the user.
Then the user can send their secure contents, which is first
encrypted by a key derived from the master password then encrypted
by the transport key, to [SSS](./glossary.md#sss). Now, the sequence
of storing secure contents should look like this:

```mermaid
sequenceDiagram
title Basic storing sequence of the application
actor Client
Client ->>+ IdP: Authenticate
IdP ->>- Client: Authenticate Session ID
Client ->> Client: Generate ephemeral key pair
Client ->>+ KMS: Request Transport Key with the public key
KMS ->> KMS: Generate ephemeral key pair
KMS ->> KMS: Calculate transport key
KMS ->> KMS: Store the transport key
KMS ->>- Client: Response the public key
Client ->> Client: Calculate transport key
Client ->> Client: Encrypt with master password
Client ->> Client: Encrypt with transport key
loop Storing secure contents
  Client ->>+ SSS: Send secure content
  SSS ->>+ KMS: Request re-encryption
  KMS ->> KMS: Decrypt with transport key
  KMS ->> KMS: Encrypt with SSS CEK
  KMS ->>- SSS: Response the result
  SSS ->> Storage: Store it
  SSS ->>- Client: Returning content ID
end
```

After a secure content is stored, then retrieval of the content
must also be possible. Though, [SSS](./glossary.md#sss) doesn't 
have any knowledge of the contents, unless the owner and content
ID, querying contents by search criteria is not possible. Thus,
the only ways it can provide are giving the list of content IDs,
returning all contents, and searching by content ID. So, the
retrieval sequence should look like this:

```mermaid
sequenceDiagram
title Basic retrieval sequence of the application
actor Client
participant IdP
participant KMS
participant SSS
participant Storage

Note over Client, Storage: Authentication to key agreement are the same as the storing
Note over KMS: Encryption or decryption uses the agreed transport key to encrypt

alt Retrieval by content ID
  Client ->>+ SSS: Request for all content IDs
  SSS ->> Storage: Get all content IDs
  SSS ->>+ KMS: Request to encrypt the content IDs
  KMS ->> KMS: Decrypt with CEK
  KMS ->> KMS: Encrypt with transport key
  KMS ->>- SSS: Encrypted list of content IDs
  SSS ->>- Client: Encrypted list of content IDs
  opt Content retrieval
    Client ->> Client: Decrypt the list of content IDs
    Client ->>+ SSS: Request a content with one of the IDs
    SSS ->> Storage: Retrieve content of the given ID
    SSS ->>+ KMS: Request to encrypt the content
    KMS ->> KMS: Decrypt with CEK
    KMS ->> KMS: Encrypt with transport key
    KMS ->>- SSS: Encrypted content
    SSS ->>- Client: Encrypted content
  end
else Retrieval of all contents
  Client ->>+ SSS: Request for all contents
  SSS ->> Storage: Retrieve all contents
  SSS ->>+ KMS: Request to encrypt the contents
  KMS ->> KMS: Decrypt with CEK
  KMS ->> KMS: Encrypt with transport key
  KMS ->>- SSS: Encrypted contents
  SSS ->>- Client: Encrypted contents
end
Client ->> Client: Decrypt the returned content(s)
```

As the storing sequence, stored secure contents are encrypted
by [SSS](./glossary.md#sss)'s [CEK](./glossary.md#cek), so 
[KMS](./glossary.md#kms) must decrypt with it then encrypt 
with the transport key.
