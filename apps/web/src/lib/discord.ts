const clientId = import.meta.env.VITE_DISCORD_CLIENT_ID;
const redirect = encodeURIComponent(`${window.location.origin}/auth/callback`);

export const loginUrl = `https://discord.com/api/oauth2/authorize?client_id=${clientId}&redirect_uri=${redirect}&response_type=code&scope=identify`;
