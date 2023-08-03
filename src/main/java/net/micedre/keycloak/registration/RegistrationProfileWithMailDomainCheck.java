package net.micedre.keycloak.registration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.core.MultivaluedMap;

import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.authentication.forms.RegistrationPage;
import org.keycloak.authentication.forms.RegistrationProfile;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.services.messages.Messages;
import org.keycloak.services.validation.Validation;

public class RegistrationProfileWithMailDomainCheck extends RegistrationProfile implements FormAction {

   public static final String PROVIDER_ID = "registration-mail-check-action";

   @Override
    public String getDisplayType() {
        return "Profile Validation with email domain check";
   }


   @Override
   public String getId() {
      return PROVIDER_ID;
   }

   @Override
    public boolean isConfigurable() {
        return true;
   }


   @Override
   public String getHelpText() {
      return "Adds validation of domain emails for registration";
   }

   private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();

   static {
      ProviderConfigProperty property;
      property = new ProviderConfigProperty();
      property.setName("validDomains");
      property.setLabel("Valid domain for emails");
      property.setType(ProviderConfigProperty.STRING_TYPE);
      property.setHelpText("List mail domains authorized to register, separated by '##'");
      CONFIG_PROPERTIES.add(property);
   }

   
   private static final boolean globmatches(String text, String glob) {
      if (text.length() > 200) {
         return false;
      }
      String rest = null;
      int pos = glob.indexOf('*');
      if (pos != -1) {
         rest = glob.substring(pos + 1);
         glob = glob.substring(0, pos);
      }

      if (glob.length() > text.length())
         return false;

      // handle the part up to the first *
      for (int i = 0; i < glob.length(); i++)
         if (glob.charAt(i) != '?'
                 && !glob.substring(i, i + 1).equalsIgnoreCase(text.substring(i, i + 1)))
            return false;

      // recurse for the part after the first *, if any
      if (rest == null) {
         return glob.length() == text.length();
      } else {
         for (int i = glob.length(); i <= text.length(); i++) {
            if (globmatches(text.substring(i), rest))
                return true;
         }
         return false;
      }
   }

   @Override
   public List<ProviderConfigProperty> getConfigProperties() {
      return CONFIG_PROPERTIES;
   }

   @Override
   public void validate(ValidationContext context) {
      MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

      List<FormMessage> errors = new ArrayList<>();
      String email = formData.getFirst(Validation.FIELD_EMAIL);

      boolean emailDomainValid = false;
      AuthenticatorConfigModel mailDomainConfig = context.getAuthenticatorConfig();
      String eventError = Errors.INVALID_REGISTRATION;

      if(email == null){
         context.getEvent().detail(Details.EMAIL, email);
         errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, Messages.INVALID_EMAIL));
         context.error(eventError);
         context.validationError(formData, errors);
         return;
      }
      
      String[] domains = mailDomainConfig.getConfig().getOrDefault("validDomains", "example.com").split("##");
      for (String domain : domains) {
         if (email.endsWith("@" + domain) || email.equals(domain)) {
            emailDomainValid = true;
            break;
         } else if (globmatches(email, "*@" + domain)) {
            emailDomainValid = true;
            break;
         }
      }
      if (!emailDomainValid) {
         context.getEvent().detail(Details.EMAIL, email);
         errors.add(new FormMessage(RegistrationPage.FIELD_EMAIL, Messages.INVALID_EMAIL));
      }
      if (errors.size() > 0) {
         context.error(eventError);
         context.validationError(formData, errors);
         return;

      } else {
         context.success();
      }

   }


   @Override
   public void buildPage(FormContext context, LoginFormsProvider form) {
      List<String> authorizedMailDomains = Arrays.asList(
         context.getAuthenticatorConfig().getConfig().getOrDefault("validDomains","exemple.org").split("##"));
      form.setAttribute("authorizedMailDomains", authorizedMailDomains);
   }


}