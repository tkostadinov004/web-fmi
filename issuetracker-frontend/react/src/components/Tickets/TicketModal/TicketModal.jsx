import React, { useState, useEffect } from 'react';
import './TicketModal.css';
import TicketSidebar from './TicketSidebar';
import TicketHeader from './TicketHeader';
import TicketContent from './TicketContent';

const TicketModal = ({ ticketCode, onClose }) => {
    const [ticket, setTicket] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTicketDetails = async () => {
            setIsLoading(true);
            setError(null);

            try {
                const token = localStorage.getItem('authToken');

                const response = await fetch(`http://localhost:8080/tickets/${ticketCode}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                });

                if (!response.ok) {
                    throw new Error('Не успяхме да заредим информацията за билета.');
                }

                const data = await response.json();
                setTicket(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setIsLoading(false);
            }
        };

        if (ticketCode) {
            fetchTicketDetails();
        }
    }, [ticketCode]);//everytime when ticketCode changed

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                {isLoading ? (
                    <div className="modal-loading">Зареждане на билета...</div>
                ) : error ? (
                    <div className="modal-error">
                        <p>Възникна грешка: {error}</p>
                        <button onClick={onClose} className="cancel-btn">Затвори</button>
                    </div>
                ) : ticket ? (
                    <>
                        <TicketHeader
                            ticketCode={ticket.code}
                            onClose={onClose}
                        />
                        <div className="modal-body">
                            <TicketContent
                                ticket={ticket}
                                onUpdate={(updatedTicket) => setTicket(updatedTicket)}
                            />
                            <TicketSidebar
                                ticket={ticket}
                                onUpdate={(updatedTicket) => setTicket(updatedTicket)}
                            />
                        </div>
                    </>
                ) : null}
            </div>
        </div>
    );
};

export default TicketModal;