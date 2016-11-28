package assignment7;

/**
 * Created by Eric on 11/28/16.
 */
interface OnRegisterObserver {
    public void observeRegister(RegisterResult result);
}

interface OnLoginObserver {
    public void observeLogin(LoginResult result);
}
