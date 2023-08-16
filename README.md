# Keycloak - Email domain validation for registration

This extension allows you to validate email domain used for registration in keycloak to accept or deny a finite list of domain.

You can use basic [glob syntax](https://en.wikipedia.org/wiki/Glob_(programming))
(only `*` and `?` are supported)

## How to install

The jar are deployed to Maven Central Repository here : https://repo1.maven.org/maven2/net/micedre/keycloak/keycloak-mail-whitelisting/

Simply drop the `jar` in the plugin directory, it will be automatically deployed by keycloak.

### Wildfly distribution (default until keycloak 16)

The plugin directory is `$KEYCLOAK_HOME\standalone\deployments`.

### Quarkus distribution (default since keycloak 17)

The plugin directory is `$KEYCLOAK_HOME\providers`.

## How to use

- Go to the admin console, in authentication menu. 
- Copy the registration flow
- add a new execution below "Profile Validation" and choose "Profile Validation With Email Domain Check" or "Profile Validation with domain block"
- Set the execution "Required"
- Configure this new execution with the allowed or blocked domains, otherwise, keycloak will only accept or block "exemple.org" domains
- Change the registration binding to this new flow
- Configure the realm to accept registration and verify email (this is important!)

##  Display mail domains in register forms

This extension provides the list of authorized patterns in the `authorizedMailDomains` and `unauthorizedMailDomains` attribute of the registration page.

This can be used like this : 

```
 <div class="${properties.kcLabelWrapperClass!}">
                    <label for="email" class="${properties.kcLabelClass!}">${msg("email")} (only ${authorizedMailDomains?join(", ")})</label>
 </div>
```
