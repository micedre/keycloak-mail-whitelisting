package net.micedre.keycloak.registration;

import org.keycloak.authentication.FormContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.models.AuthenticatorConfigModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistrationProfileWithMailDomainCheck extends RegistrationProfileDomainValidation {

   public static final String PROVIDER_ID = "registration-mail-check-action";

   private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

   public static String domainListConfigName = "validDomains";

   static {
      ProviderConfigProperty property;
      property = new ProviderConfigProperty();
      property.setName(domainListConfigName);
      property.setLabel("Valid domains for emails");
      property.setType(ProviderConfigProperty.STRING_TYPE);
      property.setHelpText("List mail domains authorized to register, separated by '##'");
      CONFIG_PROPERTIES.add(property);
   }

   @Override
   public String getDisplayType() {
        return "Profile Validation with email domain check";
   }

   @Override
   public String getId() {
      return PROVIDER_ID;
   }

   @Override
   public String getHelpText() {
      return "Adds validation of domain emails for registration";
   }

   @Override
   public List<ProviderConfigProperty> getConfigProperties() {
      return CONFIG_PROPERTIES;
   }

   @Override
   public void buildPage(FormContext context, LoginFormsProvider form) {
      List<String> authorizedMailDomains = Arrays.asList(
         context.getAuthenticatorConfig().getConfig().getOrDefault(domainListConfigName,DEFAULT_DOMAIN_LIST).split(DOMAIN_LIST_SEPARATOR));
      form.setAttribute("authorizedMailDomains", authorizedMailDomains);
   }

   @Override
   public String[] getDomainList(AuthenticatorConfigModel mailDomainConfig) {
      return mailDomainConfig.getConfig().getOrDefault(domainListConfigName, DEFAULT_DOMAIN_LIST).split(DOMAIN_LIST_SEPARATOR);
   }

   @Override
   public boolean isEmailValid(String email, String[] domains) {
      for (String domain : domains) {
         if (email.endsWith("@" + domain) || email.equals(domain) || globmatches(email, "*@" + domain)) {
            return true;
         }
      }

      return false;
   }
}