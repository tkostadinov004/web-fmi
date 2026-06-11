import React, { useState, useEffect } from "react";
import toastr from "../../services/toastrClient";
import { ticketService } from "../../services/ticketService";
import handleToastrError from "../../toastrUtils";

import { marked } from "marked";
import DOMPurify from "dompurify";

marked.setOptions({
  breaks: true,
  gfm: true,
});

const EditableDescription = ({ projectId, ticketCode, initialDescription, onDescriptionUpdate }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [text, setText] = useState(initialDescription || "");
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    setText(initialDescription || "");
  }, [initialDescription]);

  const handleSave = async () => {
    if (text === initialDescription) {
      setIsEditing(false);
      return;
    }

    setIsSaving(true);

    try {
      await ticketService.updateTicket(projectId, ticketCode, { description: text });
      onDescriptionUpdate(text);
      setIsEditing(false);
    } catch (error) {
      handleToastrError(error);
    } finally {
      setIsSaving(false);
    }
  };

  const handleCancel = () => {
    setText(initialDescription || "");
    setIsEditing(false);
  };

  if (isEditing) {
    return (
      <div className="editable-description-container">
        <textarea 
          className="description-textarea" 
          value={text} 
          onChange={(e) => setText(e.target.value)} 
          autoFocus 
          placeholder="Добави описание (поддържа Markdown)..." 
          disabled={isSaving} 
        />
        <div className="editable-actions">
          <button className="save-btn" onClick={handleSave} disabled={isSaving}>
            {isSaving ? "Запазване..." : "Save"}
          </button>
          <button className="cancel-btn" onClick={handleCancel} disabled={isSaving}>
            Cancel
          </button>
        </div>
      </div>
    );
  }

  const rawMarkup = initialDescription ? marked.parse(initialDescription) : "";
  const safeMarkup = DOMPurify.sanitize(rawMarkup);

  return (
    <div className="description-display" onClick={() => setIsEditing(true)}>
      {initialDescription ? (
        <div 
          className="markdown-body" 
          dangerouslySetInnerHTML={{ __html: safeMarkup }} 
        />
      ) : (
        <span className="placeholder-text">Добави описание (поддържа Markdown)...</span>
      )}
    </div>
  );
};

export default EditableDescription;