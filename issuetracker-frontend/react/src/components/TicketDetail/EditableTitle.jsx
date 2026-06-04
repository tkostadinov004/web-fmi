import React, { useState, useEffect } from 'react';
import { ticketService } from '../../services/ticketService';

const EditableTitle = ({ projectId, ticketCode, initialTitle, onTitleUpdate }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [titleText, setTitleText] = useState(initialTitle || '');
    const [isSaving, setIsSaving] = useState(false);

    useEffect(() => {
        setTitleText(initialTitle || '');
    }, [initialTitle]);

    const handleSave = async () => {
        if (!titleText.trim() || titleText === initialTitle) {
            setIsEditing(false);
            setTitleText(initialTitle);
            return;
        }

        setIsSaving(true);
        try {
            await ticketService.updateTicket(projectId, ticketCode, { title: titleText });
            onTitleUpdate(titleText);
            setIsEditing(false);
        } catch (error) {
            console.error(error);
            alert('Възникна грешка при запазването на заглавието.');
        } finally {
            setIsSaving(false);
        }
    };

    const handleCancel = () => {
        setTitleText(initialTitle || '');
        setIsEditing(false);
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSave();
        } else if (e.key === 'Escape') {
            handleCancel();
        }
    };

    if (isEditing) {
        return (
            <div className="editable-title-container">
                <input
                    type="text"
                    className="title-input"
                    value={titleText}
                    onChange={(e) => setTitleText(e.target.value)}
                    onKeyDown={handleKeyDown}
                    autoFocus
                    disabled={isSaving}
                />
                <div className="editable-title-actions">
                    <button className="icon-save-btn" onClick={handleSave} disabled={isSaving} title="Запази (Enter)">
                        <i className="fa-solid fa-check"></i>
                    </button>
                    <button className="icon-cancel-btn" onClick={handleCancel} disabled={isSaving} title="Отказ (Esc)">
                        <i className="fa-solid fa-xmark"></i>
                    </button>
                </div>
            </div>
        );
    }

    return (
        <h1 
            className="title-display" 
            onClick={() => setIsEditing(true)}
            title="Кликни за редакция"
        >
            {initialTitle}
        </h1>
    );
};

export default EditableTitle;