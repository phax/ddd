# ddd

Document Details Determinator - determine VESIDs from payload to use with https://github.com/phax/phive and https://github.com/phax/phive-rules

This project helps to determine the VESID from the payload.
Currently only XML payloads are supported.

# How does it work

A set of relevant fields for business document is defined. Per version 1.0 these are:
* Customization ID
* Process ID
* Sender ID
* Receiver ID
* Business Document ID
* Sender Name
* Receiver Name

These fields are to be determined differently depending on a syntax.
DDD offers a mapping of these fields on a set of predefined syntaxes:
* UBL 2.x Invoice
* UBL 2.x Credit Note
* CII D16B
