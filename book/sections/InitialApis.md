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

[^1]: Apparently, recrypt is already a used term as we are using.
      I thought I've made it... 
