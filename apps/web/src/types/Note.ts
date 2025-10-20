export interface Note {
  id: string;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
  serverId: number;
  channelId: number;
  discordUserId: number;
  visibility: string;
  alertAt?: string;
}
