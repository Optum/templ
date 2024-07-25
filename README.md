# templ

## Easy Handling of Delimiters that Accompany Optional Data

This library provides a simple solution to a common use case in configuration variables:
> _A delimiter is needed to accompany optional data, but must not be present when the optional data is missing._

Traditionally this scenario is solved in one of two ways:
* Treat the delimiter as required, forcing a default value to always be defined and available.
* Embed the optional delimiter in the actual value of the optional data, essentially _corrupting_ the value of the data (`PORT="80"` becomes `PORT=":80"` but clearly the value `:80` is not a valid port number.)

## templ - Is a Primitive Library
Don't expect to use `templ` without writing a little code around it: it is primitive.  This library Does One Thing: the substitution processing.  You'll need to provide the bindings into your context and use case.

## templ - Solution
The solution pattern provided by `templ` is to include the delimiter(s) inside the double-braces template substitution syntax `{{}}` (as a prefix and/or a suffix).

### Case 1: Undefined value - delimiter is omitted
```
API_HOST="api.example.com"
API_ENDPOINT="{{API_HOST}}{{:API_PORT}}"
```
#### Result: `api.example.com`
(With `API_PORT` undefined, the `:` is excluded from the result.)

### Case 2: Defined value - delimiter is included
```
API_HOST="api.example.com"
API_PORT="8080"
API_ENDPOINT="{{API_HOST}}{{:API_PORT}}"
```
#### Result: `api.example.com:8080`
(With `API_PORT` defined, the `:` is included in the result.)

## Supported Delimiters
There are eighteen supported delimiters:

1. ` <space> `
2. ` , `
3. ` . `
4. ` ; `
5. ` : `
6. ` ? `
7. ` & `
8. ` @ `
9. ` # `
10. ` / `
11. ` ( `
12. ` ) `
13. ` < `
14. ` > `
15. ` _ `
16. ` - `
17. ` \ `
18. ` | `

### Delimiter Rules
* Delimiters can be used as a prefix and/or suffix.
* Any number of delimiters can be used in any combination.

## Nested Evaluation (Dynamic Variables)
Variables can be dynamic.

### Example 1:
```
INSTANCE="3"
HOST_1="devhost1.example.com"
HOST_2="devhost2.example.com"
HOST_3="qa.example.com"
HOST_4="example.com"
SERVICE_ENDPOINT="{{HOST_{{INSTANCE}}}}"
```

This usefulness is compounded when multiple variables are resolved:

### Example 1:
```
INSTANCE="3"
HOST_1="devhost.example.com"
PORT_1="8080"
HOST_2="devhost.example.com"
PORT_2="8081"
HOST_3="qa.example.com"
HOST_4="example.com"
SERVICE_ENDPOINT="{{HOST_{{INSTANCE}}}}{{:PORT_{{INSTANCE}}}}"
```

## UPPERCASE
The value returned by a template evaluation can be converted to uppercase using the `^` character before the variable name.

### Example:
```
ENV="dev"
HOST_DEV="vm123.example.com"
HOST_QA="qa.example.com"
HOST_PROD="example.com"
SERVICE_ENDPOINT="{{HOST_{{^ENV}}}}"
```
> This example shows both the `^` UPPERCASE syntax, and the nested-template resolution to resolve values dynamically.