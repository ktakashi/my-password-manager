Initial APIs
============

We made a basic flow sequence. It's time to decide the APIs.
I'm not a REST fundamentalist, so the APIs may not follow
REST principal strictly. Also, I personally think providing
only REST APIs is not enough as the application is not only
providing resource access. So, the application will provide
pragmatic APIs to achieve our goals.

Let's start with [SSS](./glossary.md#sss), which is the goal
of our application. [SSS](./glossary.md#sss) will provide 2
types of APIs, creation and retrieval. It seems these 2 APIs
can be more or less RESTish APIs. So, it may look like this:

| API endpoint                            | Description                                                   |
|-----------------------------------------|---------------------------------------------------------------|
| GET  /{pseudonym}/contents              | Retrieve all contents of the user associated to {pseudonym}   |
| POST /{pseudonym}/contents              | Create a secure content of the user associated to {pseudonym} |
| GET  /{pseudonym}/contents/{content-id} | Retrieve a content of ID {content-id}                         |
| PUT  /{pseudonym}/contents/{content-id} | Update a content of ID {content-id}                           |

`GET /{pseudonym}/contents` accepts query parameter named 
`fields` to retrieve only known fields, such as `id`.

For the entire API definition, 
see [SSS version 0.0.1 OAS](../../app/api-definitions/sss-0.0.1.yaml).

Now, we put `{pseudonym}` instead of `{user-id}`. If we put `{user-id}`,
then the value might be internal ID or a value something you can indentify
who the actual person is, such as E-mail address. Neither of them are
suitable for our purpose as we want to build a secure application 
without any information breach. Pseudonym can be a randomly allocated
value, possibly a short period of time or per session. For the initial
setup, we keep `{pseudonym}` just randomly allocated aliases. We will
revisit this topic in later section.

So, we decided the [SSS](./glossary.md#sss)'s APIs. Let's make 
[KMS](./glossary.md#kms)'s APIs as well. What we need at this stage
is that key exchange and the endpoint called by [SSS](./glossary.md#sss),
which does decryption then encryption. Let's call the latter case
_recrypt_[^1]. The key exchange endpoint should not transport the
exchanged key itself, to do so, we can use key-agreement protocol.
We use Diffie–Hellman for our application. This means, a requestor
must send its public key, possibly ephemeral one, and receive a
public of the [KMS](./glossary.md#kms), then compute the shared
secret key. The recrypt endpoint takes 3 inputs, encryption algorithm,
decryption algorithm, then the content. The algorithm contains
key ID, encryption algorithm name, and cipher parameters.

| API endpoint   | Description                                |
|----------------|--------------------------------------------|
| POST /exchange | Create shared secret key                   |
| POST /recrypt  | Decrypt then encrypt the requested content |

For the entire API definition,
see [KMS version 0.0.1 OAS](../../app/api-definitions/kms-0.0.1.yaml).


[^1]: Apparently, recrypt is already a used term as we are using.
      I thought I've made it... 

We have one more component which we need to decide its API. And that
is [IdP](./glossary.md#idp). [IdP](./glossary.md#idp) needs to provide
an authentication method. There are variety of authentication methods,
however, at this moment we choose the simple one, password authentication.
Password authentication requires a user ID and its password. So, the
API should take at least 2 arguments. Now, we want to achieve 
Zero-Knowledge Encryption, which means the [IdP](./glossary.md#idp)
must not take plain password. I'm not entirely sure if Zero-Knowledge
Encryption also means Zero-Knowledge for the authentication. But it'd
be more interesting if the wall is higher, so let's do it like that.

Not sending password but authenticating via password sounds very
conflicting. We need to make the raw password something completely
random, so that nobody can guess what the original value is. If I
write the definition like this, it seems I can use a digest. Since
clients of our application requires cryptographic operations, let's
digest the password at the client side, then send it to 
[IdP](./glossary.md#idp). If we simply digest the password, there
might be the same value, for example `password` is the most commonly
used password in 2022 unfortunately[^2], this might be a very easy to 
break through with rainbow table. So we need salt to make the 
result different. How about using the user ID as the salt. So
the password we send will be generated like this.

```
Digest(userID || password)
```

[IdP](./glossary.md#idp) doesn't know the above formula, this needs
to be done client side. And [IdP](./glossary.md#idp) simply checks
the provided user ID and password. Considering this, the API of
[IdP](./glossary.md#idp) should look like this:

| API endpoint       | Description                         |
|--------------------|-------------------------------------|
| POST /authenticate | Authenticate the user with password |

For the entire API definition,
see [IdP version 0.0.1 OAS](../../app/api-definitions/idp-0.0.1.yaml).

[^2]: https://www.cnbc.com/2022/11/23/most-common-passwords-of-2022-make-sure-yours-isnt-on-the-list.html
