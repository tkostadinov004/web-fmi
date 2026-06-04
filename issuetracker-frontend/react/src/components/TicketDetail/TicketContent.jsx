import React from 'react';
import EditableDescription from './EditableDescription';
import TicketComments from './TicketComments';
import EditableTitle from './EditableTitle';

const TicketContent = ({ projectId, ticket, onUpdate }) => {
  return (
    <div className="modal-main">

      <EditableTitle
        projectId={projectId}
        ticketCode={ticket.code}
        initialTitle={ticket.title}
        onTitleUpdate={(newTitle) => {
          onUpdate({ ...ticket, title: newTitle });
        }}
      />

      <div className="section">
        <h3>Описание</h3>
        <EditableDescription
          projectId={projectId}
          ticketCode={ticket.code}
          initialDescription={ticket.description}
          onDescriptionUpdate={(newDescription) => {
            onUpdate({ ...ticket, description: newDescription });
          }}
        />
      </div>

      <div className="section">
        <h3>Коментари</h3>
        <TicketComments
          projectId={projectId}
          ticketCode={ticket.code} 
        />
      </div>

    </div>
  );
};

export default TicketContent;