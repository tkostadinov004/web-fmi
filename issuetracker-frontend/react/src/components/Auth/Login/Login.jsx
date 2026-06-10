import React, { useState, useEffect } from "react";
import toastr from "../../../services/toastrClient";
import { useNavigate, Link } from "react-router-dom";
import "../AuthShared.css";
import "./Login.css";
import { authService } from "../../../services/authService";
import handleToastrError from "../../../toastrUtils";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      navigate("/home", { replace: true });
    }
  }, [navigate]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const token = await authService.login(username, password);

      localStorage.setItem("accessToken", token);
      localStorage.setItem("currentUsername", username);
      navigate("/home");
    } catch (error) {
      handleToastrError(error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Log in to your account</h2>

      <form onSubmit={handleLogin} className="auth-form">
        <div className="form-group">
          <label htmlFor="username">Username</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            autoComplete="username"
            placeholder="Enter your username"
            pattern="^[a-zA-Z0-9_.\-]{3,20}$"
            title="Username must be between 3 and 20 characters and contain only Latin letters, numbers, dashes, dots, or underscores."
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>
          <div className="password-input-wrapper">
            <input
              type={showPassword ? "text" : "password"}
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="current-password"
              placeholder="Enter your password"
              pattern=".{6,}"
              title="Password must be at least 6 characters long."
            />
            <button type="button" className="toggle-password-btn" onClick={() => setShowPassword(!showPassword)}>
              {showPassword ? "Hide" : "Show"}
            </button>
          </div>
        </div>

        <Link to="/forgot-password" className="forgot-password-link">
          Forgot password?
        </Link>

        <button type="submit" disabled={isLoading || !username || !password}>
          {isLoading ? "Logging in..." : "Log In"}
        </button>

        <div className="login-extras">
          <span>
            Don't have an account? <Link to="/register">Sign up</Link>
          </span>
        </div>
      </form>
    </div>
  );
};

export default Login;
