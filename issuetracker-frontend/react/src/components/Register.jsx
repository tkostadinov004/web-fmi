import "./Register.css";

function Register() {
  return (
    <>
      <div className="auth-container">
        <div className="card">
          <div className="auth-left">
            <div className="auth-left-content">
              <h1>Get Started</h1>
              <p>Already have an account?</p>

              <button className="login-btn">Log in</button>
            </div>
          </div>

          <div className="auth-right">
            <div className="form-card">
              <h2>Create account</h2>

              <form>
                <div className="form-row">
                  <div className="input-group">
                    <label>First Name</label>
                    <input type="text" name="firstName" />
                  </div>

                  <div className="input-group">
                    <label>Last Name</label>
                    <input type="text" name="lastName" />
                  </div>
                </div>

                  <div className="input-group">
                    <label>Username</label>
                    <input type="text" name="username" />
                  </div>

                  <div className="input-group">
                    <label>Company Name</label>
                    <input type="text" name="companyName" />
                  </div>

                <div className="input-group">
                  <label>Email</label>
                  <input type="email" name="email" />
                </div>

                <div className="input-group">
                  <label>Password</label>
                  <input type="password" name="password" />
                </div>
                {/* This should have NavLink tag for the transition to the login page */}
                <button type="submit" className="signup-btn">
                  Sign up
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default Register;
