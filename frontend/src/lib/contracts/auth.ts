export type Role = "ADMIN" | "HEAD" | "MEMBER" | "ANALYST";
export type UserStatus = "ACTIVE" | "INACTIVE" | "DISABLED";

export type TokenResponse = {
  accessToken: string;
  tokenType: "Bearer" | string;
};

export type UserSummaryResponse = {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  role: Role;
  status: UserStatus;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type ChangePasswordRequest = {
  currentPassword: string;
  newPassword: string;
};

export type ProvisionUserRequest = {
  email: string;
  role: Role;
  firstName: string;
  lastName: string;
  birthDate: string;
};

export type ProvisionUserResponse = {
  userId: string;
};