package net.micedre.keycloak.registration;

import org.keycloak.authentication.FormContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.models.AuthenticatorConfigModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistrationUserCreationWithDomainBlock extends RegistrationUserCreationDomainValidation {

   public static final String PROVIDER_ID = "registration-domain-block-action";

   private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

   public static String domainListConfigName = "invalidDomains";

   static {
      ProviderConfigProperty property;
      property = new ProviderConfigProperty();
      property.setName(domainListConfigName);
      property.setLabel("Invalid domain for emails");
      property.setType(ProviderConfigProperty.STRING_TYPE);
      property.setHelpText("List mail domains not authorized to register, separated by '##'");
      CONFIG_PROPERTIES.add(property);
   }

   @Override
   public String getDisplayType() {
        return "Profile Validation with domain block";
   }

   @Override
   public String getId() {
      return PROVIDER_ID;
   }

   @Override
   public String getHelpText() {
      return "Adds validation of not accepted domain emails for registration";
   }

   @Override
   public List<ProviderConfigProperty> getConfigProperties() {
      return CONFIG_PROPERTIES;
   }

   @Override
   public void buildPage(FormContext context, LoginFormsProvider form) {
      List<String> unauthorizedMailDomains = Arrays.asList(
         context.getAuthenticatorConfig().getConfig().getOrDefault(domainListConfigName, DEFAULT_DOMAIN_LIST).split(DOMAIN_LIST_SEPARATOR));
      form.setAttribute("unauthorizedMailDomains", unauthorizedMailDomains);
   }

   @Override
   public String[] getDomainList(AuthenticatorConfigModel mailDomainConfig) {
      return mailDomainConfig.getConfig().getOrDefault(domainListConfigName, DEFAULT_DOMAIN_LIST).split(DOMAIN_LIST_SEPARATOR);
   }

   @Override
   public boolean isEmailValid(String email, String[] domains) {
      for (String domain : domains) {
         if (email.endsWith("@" + domain) || email.equals(domain) || globmatches(email, "*@" + domain)) {
            return false;
         }
      }

      return true;
   }
}
