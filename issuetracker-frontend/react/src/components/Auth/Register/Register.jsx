import { useEffect, useState } from "react";
import "./Register.css";
import { NavLink } from "react-router-dom";

const Register = () => {
  useEffect(() => {
    document.body.classList.add("register-body");

    return () => {
      document.body.classList.remove("register-body");
    };
  }, []);

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [username, setUsername] = useState("");
  const [companyName, setCompanyName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const handleRegister = async (e) => {
    e.preventDefault();
    setErrorMessage("");

    try {
      // console.log(
      //   JSON.stringify({
      //     firstName,
      //     lastName,
      //     username,
      //     email,
      //     companyName,
      //     password,
      //   }),
      // );
      const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          firstName,
          lastName,
          username,
          email,
          companyName,
          password,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        navigate("/login");
      } else {
        setErrorMessage(data.error || "An error occurred during register.");
      }
    } catch (error) {
      setErrorMessage("Server connection problem.");
    }
  };

  return (
    <>
      <div className="auth-container">
        <div className="card">
          <div className="auth-left">
            <div className="auth-left-content">
              <h1>Get Started</h1>
              <p>Already have an account?</p>

              <NavLink to="login" className="login-btn">
                Log in
              </NavLink>
            </div>
          </div>

          <div className="auth-right">
            <div className="form-card">
              <h2>Create account</h2>

              <form onSubmit={handleRegister}>
                {errorMessage && (
                  <div className="error-message">{errorMessage}</div>
                )}
                <div className="form-row">
                  <div className="input-group">
                    <label>First Name</label>
                    <input
                      type="text"
                      name="firstName"
                      required
                      placeholder="Enter your first name"
                      onChange={(e) => setFirstName(e.target.value)}
                    />
                  </div>

                  <div className="input-group">
                    <label>Last Name</label>
                    <input
                      type="text"
                      name="lastName"
                      required
                      placeholder="Enter your last name"
                      onChange={(e) => setLastName(e.target.value)}
                    />
                  </div>
                </div>

                <div className="input-group">
                  <label>Username</label>
                  <input
                    type="text"
                    name="username"
                    required
                    autoComplete="username"
                    placeholder="Enter your username"
                    pattern="^[a-zA-Z0-9_.\-]{3,20}$"
                    title="Username must be between 3 and 20 characters and contain only Latin letters, numbers, dashes, dots, or underscores."
                    onChange={(e) => setUsername(e.target.value)}
                  />
                </div>

                <div className="input-group">
                  <label>Company Name</label>
                  <input
                    type="text"
                    name="companyName"
                    required
                    placeholder="Enter your company name"
                    onChange={(e) => setCompanyName(e.target.value)}
                  />
                </div>

                <div className="input-group">
                  <label>Email</label>
                  <input
                    type="email"
                    name="email"
                    required
                    placeholder="Enter your email"
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>

                <div className="input-group">
                  <label>Password</label>
                  <input
                    type="password"
                    name="password"
                    required
                    placeholder="Enter your password"
                    pattern=".{6,}"
                    onChange={(e) => setPassword(e.target.value)}
                    title="Password must be at least 6 characters long."
                  />
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
};

export default Register;
