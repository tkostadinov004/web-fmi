import React, { useState, useEffect } from 'react';
import { ticketService } from '../../services/ticketService';

const EditableDescription = ({ ticketCode, initialDescription, onDescriptionUpdate }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [text, setText] = useState(initialDescription || '');
    const [isSaving, setIsSaving] = useState(false);

    useEffect(() => {
        setText(initialDescription || '');
    }, [initialDescription]);

    const handleSave = async () => {
        if (text === initialDescription) {
            setIsEditing(false);
            return;
        }

        setIsSaving(true);

        try {
            await ticketService.updateTicket(ticketCode, { description: text });
            onDescriptionUpdate(text);
            setIsEditing(false);
        } catch (error) {
            console.error(error);
            alert('Възникна грешка при запазването. Моля, опитайте отново.');
        } finally {
            setIsSaving(false);
        }
    };

    const handleCancel = () => {
        setText(initialDescription || '');
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
                    placeholder="Добави описание..."
                    disabled={isSaving}
                />
                <div className="editable-actions">
                    <button
                        className="save-btn"
                        onClick={handleSave}
                        disabled={isSaving}
                    >
                        {isSaving ? 'Запазване...' : 'Save'}
                    </button>
                    <button
                        className="cancel-btn"
                        onClick={handleCancel}
                        disabled={isSaving}
                    >
                        Cancel
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div
            className="description-display"
            onClick={() => setIsEditing(true)}
        >
            {initialDescription ? (
                <p>{initialDescription}</p>
            ) : (
                <span className="placeholder-text">Добави описание...</span>
            )}
        </div>
    );
};

export default EditableDescription;