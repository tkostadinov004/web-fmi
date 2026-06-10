import { useEffect, useState } from "react";
import "./Register.css";
import { NavLink, useNavigate } from "react-router-dom";
import axios_client from "../../../axiosClient";

const Register = () => {
  useEffect(() => {
    document.body.classList.add("register-body");

    return () => {
      document.body.classList.remove("register-body");
    };
  }, []);

  const navigate = useNavigate();
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
      await axios_client.post("/auth/register", {
        firstName,
        lastName,
        username,
        email,
        companyName,
        password,
      });

      navigate("/login");
    } catch (error) {
      console.log(error);
      setErrorMessage("Server connection problem.");
    }
  };

  return (
    <>
      <div className="auth-container">
        <div className="register-card">
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
                  <div className="register-input-group">
                    <p>First Name</p>
                    <input
                      type="text"
                      name="firstName"
                      required
                      placeholder="Enter your first name"
                      onChange={(e) => setFirstName(e.target.value)}
                    />
                  </div>

                  <div className="register-input-group">
                    <p>Last Name</p>
                    <input
                      type="text"
                      name="lastName"
                      required
                      placeholder="Enter your last name"
                      onChange={(e) => setLastName(e.target.value)}
                    />
                  </div>
                </div>

                <div className="register-input-group">
                  <p>Username</p>
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

                <div className="register-input-group">
                  <p>Company Name</p>
                  <input
                    type="text"
                    name="companyName"
                    required
                    placeholder="Enter your company name"
                    onChange={(e) => setCompanyName(e.target.value)}
                  />
                </div>

                <div className="register-input-group">
                  <p>Email</p>
                  <input
                    type="email"
                    name="email"
                    required
                    placeholder="Enter your email"
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>

                <div className="register-input-group">
                  <p>Password</p>
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
