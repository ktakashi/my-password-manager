Initial Sequence
================

We've designed the initial components, now we need to design
the operation sequence. The first step is always easy, a user
get authenticated. Once the user is authenticated, then the
user needs to get a transport key. A transport key is a secret
key which needs to be shared by both the application and the user.
Then the user can send their secure contents, which is first
encrypted by a key derived from the master password then encrypted
by the transport key, to [SSS](./glossary.md#sss). The sequence
should look like this:

```mermaid
sequenceDiagram
title Basic sequence of the application
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
  SSS ->>- Client: OK
end
```

