import React, { useState, useEffect } from 'react';
import './TicketModal.css';
import TicketSidebar from './TicketSidebar';
import TicketHeader from './TicketHeader';
import TicketContent from './TicketContent';
import { ticketService } from '../../services/ticketService';

const TicketModal = ({ ticketCode, onClose }) => {
    const [ticket, setTicket] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTicketDetails = async () => {
            setIsLoading(true);
            setError(null);

            try {
                const data = await ticketService.getTicket(ticketCode);
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
    }, [ticketCode]); // everytime when ticketCode changed

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
                            projectUuid={ticket.projectUuid}// за workflow-a
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