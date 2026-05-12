import React from 'react';
import EditableDescription from './EditableDescription';
import TicketComments from './TicketComments';

const TicketContent = ({ ticket, onUpdate }) => {
  return (
    <div className="modal-main">
      
      <h1 className="ticket-title">{ticket.title}</h1>

      <div className="section">
        <h3>Описание</h3>
        <EditableDescription 
          ticketCode={ticket.code} 
          initialDescription={ticket.description} 
          onDescriptionUpdate={(newDescription) => {
            onUpdate({ ...ticket, description: newDescription });
          }}
        />
      </div>

      <div className="section">
        <h3>Коментари</h3>
        <TicketComments ticketCode={ticket.code} />
      </div>

    </div>
  );
};

export default TicketContent;