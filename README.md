# cryogen-admin-ui-md

A Clojure library to be used with cryogen static site generator

This could be used to create and edit posts or pages with a simple ui instead of directly editing the files

## Usage

add the following dependency to the project
```
	 [cryogen-admin-ui-md "0.1.0-SNAPSHOT"]
```
then add the following profile to the project

```
:profiles {:admin 
             {:ring {:handler cryogen-admin-ui-md.handler/handler-with-admin}}}
```
then in cmd run

```
lein with-profile admin ring server
```

now you can access admin ui at http://localhost:3000/admin


## License

Copyright © 2018 Nagarajan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
