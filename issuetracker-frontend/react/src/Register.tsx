import './Register.css'

function Register() {
    return (<>
        <div className="auth-container">
            <div className="auth-left">
                <div className="auth-left-content">
                    {/* This should be changed to NavLink */}
                    <p className="back-link">← Home Page</p> 

                    <h1>Get Started</h1>
                    <p>Already have an account?</p>

                    <button className="login-btn">Log in</button>
                </div>
            </div>

            <div className="auth-right">
                <div className="form-card">
                    <div className="form-header">
                        <span>Need help?</span>
                    </div>

                    <h2>Create account</h2>

                    <form>
                        <label>Email</label>
                        <input type="email" placeholder="Enter your email" />

                        <label>Password</label>
                        <input type="password" placeholder="Enter your password" />

                        <label>Full Name</label>
                        <input type="text" placeholder="Enter your name" />

                        <div className="checkbox">
                            <input type="checkbox" id="terms" />
                            <label htmlFor="terms">
                                I accept the terms of the agreement
                            </label>
                        </div>
                        {/* Here also should have a NavLink tag for the transition */}
                        <button type="submit" className="signup-btn">
                            Sign up
                        </button>
                    </form>
                </div>
            </div>
        </div>


    </>)
}

export default Register