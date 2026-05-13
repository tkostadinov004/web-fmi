const getInitials = (name) =>
  name
    .split(" ")
    .map((part) => part[0])
    .join("");

export const UserAvatar = ({ name }) => (
  <div className="user-avatar" title={name}>
    {getInitials(name)}
  </div>
);

export const UserAvatarList = ({ users }) => (
  <div className="users-list">
    {users.map((user) => (
      <UserAvatar key={user} name={user} />
    ))}
  </div>
);
