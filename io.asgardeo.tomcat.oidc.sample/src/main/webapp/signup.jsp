<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="theme.css">
</head>
<body>
<div class="page-wrapper right-aligned">
    <main class="center-segment">
        <div class="slogan-container">
            <div class="ui container medium aligned middle aligned">
                <div class="ui segment">
                    <div class="signup-form">
                        <form class="ui large form pfy-signup-form"
                                                      data-testid="early-signup-page-form"
                                                      action="/oidc-sample-app/register/local"
                                                      id="pfy_signup_form"
                                                      method="post">
                            <input autocomplete="email"
                            data-testid="early-signup-page-form-email-address-field"
                            minlength="3"
                            maxlength="50"
                            placeholder="Enter your email"
                            name="email"
                            type="text"
                            class="mandatory-input">
                        </br>
                            <input autocomplete="password"
                            data-testid="early-signup-page-form-password-field"
                            minlength="3"
                            maxlength="50"
                            placeholder="Enter your Password"
                            name="password"
                            type="text"
                            class="mandatory-input">

                            <div class="mt-4">
                                <div class="button">
                                    <button id="btn-submit" class="ui primary fluid large button disabled"
                                            type="submit"
                                            value="submit"
                                    >
                                        SignUp
                                    </button>
                                </div>
                                <!-- Social Login section -->
                                <div class="ui horizontal divider">
                                    Or
                                </div>

                            <div class="mt-4">
                                </div>
                                <div class="social-login social-dimmer mt-5">
                                    <div class="buttons mb-3" data-position="bottom center">
                                        <button
                                                type="button"
                                                class="ui basic button"
                                                onclick="signUpWithGoogle()"
                                                data-testid="registration-page-sign-in-with-google"
                                        >

                                            <span>Sign up with Google</span>
                                        </button>
                                    </div>
                                </br>
                                    <div class="buttons mb-3" data-position="bottom center">
                                        <button
                                                type="button"
                                                class="ui basic button"
                                                onclick="signUpWithGitHub()"
                                                data-testid="registration-page-sign-in-with-github"
                                        >

                                            <span>Sign up with GitHub</span>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                        </div>
                    </div>
                </div>
            </div>  
        </main>
</div>

<script type="text/javascript">

function signUpWithGoogle() {

    var url = "/oidc-sample-app/register/federated?fidp=google";
    window.location.href = url;
}
</script>

</body>