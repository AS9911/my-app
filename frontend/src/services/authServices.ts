import apiClient from "../api/apiClient";

export interface LoginRequest {
    userId: string;
    password: string;
}

export const login = async (request: LoginRequest) => {
   const response = await apiClient.post(
       "/auth/login",
       request
   );

   return response;
}