# Keycloak - Restrict email domain for registration

This extension allows you to validate email domain used for registration in keycloak to accept only a finite list of domain.

## How to install

Simply drop the jar in `$KEYCLOAK_HOME\standalone\deployments`, it will be automatically deployed by keycloak.

## How to use

- Go to the admin console, in authentication menu. 
- Copy the registration flow
- add a new execution below "Profile Validation" and choose "Profile Validation With Email Domain Check"
- Configure this new execution (otherwise, keycloak will only accept "exemple.org" domains)
- Change the registration binding to this new flow
- Configure the realm to accept registration and verify email (this is important!)