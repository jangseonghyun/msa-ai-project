import api from "./api";
import { setAccessToken } from "./token";

export const login = async (id, pw) => {
    const res = await api.post("/auth/login", {id, pw});

    setAccessToken(res.data.accessToken);

    return res.data;
}