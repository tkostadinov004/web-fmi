import React from 'react';
import { useSearchParams } from "react-router-dom"; 
import { StatusBadge, PriorityBadge } from "./Badge";
import TicketModal from '../TicketDetail/TicketModal';

export const TicketTable = ({ tickets }) => {
  const [searchParams, setSearchParams] = useSearchParams();

  const selectedTicketCode = searchParams.get('ticket');

  const handleOpenModal = (code) => {
    setSearchParams({ ticket: code });
  };

  const handleCloseModal = () => {
    setSearchParams({});
  };

  return (
    <div className="table-container">
      <table>
        <thead>
          <tr>
            <th>Ticket</th>
            <th>Assignee</th>
            <th>Priority</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>
          {tickets.map((ticket) => (
            <tr key={ticket.code}>
              <td>
                <div 
                  onClick={() => handleOpenModal(ticket.code)}
                  style={{ cursor: 'pointer' }}
                >
                  <div className="table-ticket-id" style={{ color: 'var(--hunter-green)', textDecoration: 'underline' }}>
                    {ticket.code}
                  </div>
                  <div className="table-ticket-title">{ticket.title}</div>
                </div>
              </td>

              <td>{ticket.assignee?.username ?? '—'}</td>

              <td>
                <PriorityBadge priority={ticket.ticketPriority} />
              </td>

              <td>
                <StatusBadge status={ticket.ticketStatus} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedTicketCode && (
        <TicketModal 
          ticketCode={selectedTicketCode} 
          onClose={handleCloseModal} 
        />
      )}
    </div>
  );
};
