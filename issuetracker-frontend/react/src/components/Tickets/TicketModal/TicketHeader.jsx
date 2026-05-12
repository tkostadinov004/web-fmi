import React from 'react';

const TicketHeader = ({ ticketCode, onClose }) => {
  return (
    <div className="modal-header">

      <div className="ticket-breadcrumb">
        <i className="fa-solid fa-ticket" style={{ color: 'var(--sage-green)' }}></i>
        <span>{ticketCode}</span>
      </div>

      <div className="header-actions">

        <button
          className="workflow-placeholder-btn"
          onClick={() => alert('Тук по-късно ще изскача твоят отделен Workflow модал/компонент!')}
          title="Управление на workflow-a"
        >
          <i className="fa-solid fa-code-branch"></i> Workflow
        </button>

        <button className="icon-btn" title="Прикачи файл">
          <i className="fa-solid fa-paperclip"></i>
        </button>
        <button className="icon-btn" title="Копирай линк">
          <i className="fa-solid fa-link"></i>
        </button>

        <button className="close-btn" onClick={onClose} title="Затвори (Esc)">
          <i className="fa-solid fa-xmark"></i>
        </button>
      </div>

    </div>
  );
};

export default TicketHeader;