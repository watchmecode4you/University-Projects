import java.util.regex.Pattern;

public class CheckUps {
	public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    // Method for checking the validity of password and the password confirmation
    public boolean isValidPassword(String passwordhere, String confirmhere) {

        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        boolean flag=true;

        if (!passwordhere.equals(confirmhere)) {
            flag=false;
        }
        if (passwordhere.length() < 8) {         
            flag=false;
        }
        if (!specailCharPatten.matcher(passwordhere).find()) {          
            flag=false;
        }
        if (!UpperCasePatten.matcher(passwordhere).find()) {            
            flag=false;
        }
        if (!lowerCasePatten.matcher(passwordhere).find()) {
            flag=false;
        }
        if (!digitCasePatten.matcher(passwordhere).find()) {
            flag=false;
        }
        return flag;

    }
}
