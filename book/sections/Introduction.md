Introduction
============

In December 2022, there was a major incident of one of the most famous
password manager vendors. In short, there was malicious access on their
encrypted storage, and they confirmed that the contents of the storage
was illegally retrieved. In their comments, the contents is encrypted 
with AES-256 and the key is derived from the customers' password, which
they have zero knowledge about. This model is called Zero-Knowledge
Encryption, which basically only users can access their secret contents,
in other words, only users know their encryption key or master password.

In general, brute force decrypting a cipher text encrypted by AES-256 
takes a lot of times. Some say it's not possible to do it within a
reasonable amount of time[^1]. Although that's based on the fact that
brute force uses entire byte, which is 256 bits, however, if you use
a password to derive a key, then you can only use maximum of 94 bits 
as the range of 0x00-0x19 and 0x7F-0xFF are control characters. To
consider this, some say, brute force attack can be done slightly more
than 2 months[^2].

Now, how can a customer service achieve Zero-Knowledge Encryption. Most
of the services, including password managers, require user credentials 
to log in their service. Deriving an encryption key from the password
on the client side can be achieved easily by using [KDF](./glossary.md#KDF).
However, not sending the master password to log in sounds a bit more
complicated. We will figure it out when the [IdP](./glossary.md#IdP) 
application.

[^1]: [How long would it take to brute force AES-256?](https://scrambox.com/article/brute-force-aes/)
[^2]: [What’s in a PR statement: LastPass breach explained](https://palant.info/2022/12/26/whats-in-a-pr-statement-lastpass-breach-explained/)

Disclaimer
----------

I am trying to write things right as much as possible. I am not a
security specialist but mere a software developer interested in
cryptography and security. This book may contain uncommon practices
or overlooked vulnerabilities. Using this book is at your own risk.

Next: [Initial Component](./InitialComponent.md)
