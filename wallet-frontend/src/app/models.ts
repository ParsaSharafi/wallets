export interface IUser {
  username?: string;
  password?: string;
}

export interface IResponse {
  isSuccessful: boolean;
  data: any;
}

export interface IWallet {
  tag: string;
  balance: number;
  created: string;
}

export interface ICurrency {
  code: string;
  rate: number;
}

export interface IFees {
  exchangeFee?: number;
  transferFee?: number;
}

export interface ITransaction {
  signedAmount: number;
  info: string;
  initiated: string;
  status: string;
}

export interface ISendMoney {
  sourceWalletTag?: string;
  amount?: number;
  destinationUsername?: string;
  destinationWalletTag?: string;
}

export interface IAddWallet {
  name?: string;
  currency?: string;
}
