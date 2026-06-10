import React, { useState, useEffect } from "react";
import toastr from "../../services/toastrClient";
import CommentItem from "./CommentItem";
import { ticketService } from "../../services/ticketService";
import handleToastrError from "../../toastrUtils";

const TicketComments = ({ projectId, ticketCode }) => {
  const [comments, setComments] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [newCommentText, setNewCommentText] = useState("");
  const [isPosting, setIsPosting] = useState(false);

  const fetchComments = async () => {
    setIsLoading(true);
    try {
      const data = await ticketService.getComments(projectId, ticketCode);
      setComments(data);
    } catch (error) {
      handleToastrError(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (projectId && ticketCode) {
      fetchComments();
    }
  }, [projectId, ticketCode]);

  const handlePostComment = async () => {
    if (!newCommentText.trim()) return;

    setIsPosting(true);
    try {
      await ticketService.addComment(projectId, ticketCode, newCommentText);
      setNewCommentText("");
      fetchComments();
    } catch (error) {
      handleToastrError(error);
    } finally {
      setIsPosting(false);
    }
  };

  const handleCommentUpdatedOrDeleted = () => {
    fetchComments();
  };

  return (
    <div className="ticket-comments-section">
      <div className="add-comment-box">
        <div className="add-comment-avatar">
          {/* профилната снимка на логнатия потребител */}
          <i className="fa-solid fa-user"></i>
        </div>
        <div className="add-comment-input-area">
          <textarea className="comment-textarea" placeholder="Добави коментар..." value={newCommentText} onChange={(e) => setNewCommentText(e.target.value)} disabled={isPosting} />

          {newCommentText.trim() && (
            <div className="comment-actions">
              <button className="save-btn" onClick={handlePostComment} disabled={isPosting}>
                {isPosting ? "Запазване..." : "Запази"}
              </button>
              <button className="cancel-btn" onClick={() => setNewCommentText("")} disabled={isPosting}>
                Отказ
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="comments-list">
        {isLoading ? (
          <p className="loading-text">Зареждане на коментари...</p>
        ) : comments.length > 0 ? (
          comments.map((comment) => <CommentItem key={comment.uuid} comment={comment} onUpdate={handleCommentUpdatedOrDeleted} onDelete={handleCommentUpdatedOrDeleted} />)
        ) : (
          <p className="no-comments-text">Няма добавени коментари към този билет.</p>
        )}
      </div>
    </div>
  );
};

export default TicketComments;
