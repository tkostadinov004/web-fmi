import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Login.css'; // Импортираме стиловете

const Login = () => {
    // 1. Създаваме състояния (state) за полетата и грешките
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    
    const navigate = useNavigate();

    // 2. Функция, която се изпълнява при натискане на бутона "Вход"
    const handleLogin = async (e) => {
        e.preventDefault(); // Спира презареждането на страницата

        // Изчистваме старите грешки и показваме, че зареждаме
        setErrorMessage('');
        setIsLoading(true);

        try {
            // ПРАВИМ ЗАЯВКАТА КЪМ БЕКЕНДА
            // Замени 'http://localhost:8080' с реалния порт на вашия бекенд
            const response = await fetch('http://localhost:8080/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }),
            });

            // Парсираме отговора от сървъра
            const data = await response.json();

            if (response.ok) {
                // УСПЕШЕН ВХОД (Статус 200)
                console.log("Съобщение от сървъра:", data.message);

                // we storage it in the localStorage in future we can upgrade it and store it in the httpOnly cookies
                localStorage.setItem('authToken', data.token);

                // Временно показваме alert, по-късно тук ще пренасочваме към Dashboard-a
                navigate('/dashboard');

            } else {
                // ГРЕШКА ОТ СЪРВЪРА (Статуси 400, 409, 500)
                // Взимаме текста на грешката от обекта, който бекендът ни връща
                setErrorMessage(data.error || 'Възникна непозната грешка при влизане.');
            }
        } catch (error) {
            // ГРЕШКА В МРЕЖАТА (напр. паднал сървър)
            console.error("Мрежова грешка:", error);
            setErrorMessage('Проблем с връзката със сървъра. Опитайте отново.');
        } finally {
            // Спираме индикатора за зареждане
            setIsLoading(false);
        }
    };

    // 3. Визуализацията (HTML-ът на компонента)
    return (
        <div className="login-container">
            <h2>Вход в системата</h2>

            <form onSubmit={handleLogin} className="login-form">
                {/* Ако има грешка, показваме този div */}
                {errorMessage && <div className="error-message">{errorMessage}</div>}

                <div className="form-group">
                    <label htmlFor="username">Потребителско име</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)} // Обновява state-а при писане
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password">Парола</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <button type="submit" disabled={isLoading}>
                    {isLoading ? 'Влизане...' : 'Вход'}
                </button>
            </form>
        </div>
    );
};

export default Login;