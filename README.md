# Keycloak - Whitelist email domain for registration

This extension allows you to validate email domain used for registration in keycloak to accept only a finite list of domain.

You can use basic [glob syntax](https://en.wikipedia.org/wiki/Glob_(programming))
(only `*` and `?` are supported)

## How to install

Simply drop the `jar` in the plugin directory, it will be automatically deployed by keycloak.

### Keycloak < 17

The plugin directory is `$KEYCLOAK_HOME\standalone\deployments`.

### Keycloak >= 17

The plugin directory is `$KEYCLOAK_HOME\providers`.

## How to use

- Go to the admin console, in authentication menu. 
- Copy the registration flow
- add a new execution below "Profile Validation" and choose "Profile Validation With Email Domain Check"
- Set the execution "Required"
- Configure this new execution (otherwise, keycloak will only accept "exemple.org" domains)
- Change the registration binding to this new flow
- Configure the realm to accept registration and verify email (this is important!)

##  Display authorized mail domains in register forms

This extension provides the list of authorized patterns in the `authorizedMailDomains` attribute of the registration page.

This can be used like this : 

```
 <div class="${properties.kcLabelWrapperClass!}">
                    <label for="email" class="${properties.kcLabelClass!}">${msg("email")} (only ${authorizedMailDomains?join(", ")})</label>
 </div>
```
