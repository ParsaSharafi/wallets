import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { IAddWallet, ICurrency, IResponse, ISendMoney, IUser } from './models';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  http = inject(HttpClient);
  url: string = '/api';
  token: string = '';
  pickedWallet: string = '';
  smdto: ISendMoney = {};

  currencies: ICurrency[] = [];
  _currencies = new BehaviorSubject<ICurrency[]>([]);
  currencies$: Observable<ICurrency[]> = this._currencies.asObservable();
  updateCurrencies(): void {
    this.http.get<IResponse>(this.url + '/GetCurrencies', {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.token)
    }).subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful)
          this._currencies.next(result.data);

        else
          alert(result.data);
      }
    });
  }

  _refresh = new Subject<void>();
  refresh$ = this._refresh.asObservable();
  refresh() {
    this._refresh.next();
  }

  logIn(user: IUser): Observable<IResponse> {
    return this.http.post<IResponse>(this.url + '/LogIn', user);
  }

  setToken(jwt: string): void {
    this.token = jwt;
  }

  setPickedWallet(tag: string): void {
    this.pickedWallet = tag;
  }

  getPickedWallet(): string {
    return this.pickedWallet;
  }

  isLoggedIn(): boolean {
    return this.token.length > 0;
  }

  signUp(user: IUser): Observable<IResponse> {
    return this.http.post<IResponse>(this.url + '/SignUp', user);
  }

  listWallets(): Observable<IResponse> {
    return this.http.get<IResponse>(this.url + '/ListWallets', {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.token)});
  }

  getFees(): Observable<IResponse> {
    return this.http.get<IResponse>(this.url + '/GetFees', {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.token)
    });
  }

  listTransactions(): Observable<IResponse> {
    return this.http.post<IResponse>(this.url + '/ListTransactions', this.pickedWallet, {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.token)
    });
  }

  sendMoney(dto: ISendMoney): Observable<IResponse> {

    dto.sourceWalletTag = this.pickedWallet;
    dto.amount = dto.amount ? Math.round(dto.amount * 100) : 0;
    dto.destinationUsername = dto.destinationUsername ? dto.destinationUsername : '';

    return this.http.post<IResponse>(this.url + '/SendMoney', dto, {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.token)
    });
  }

  addWallet(dto: IAddWallet): Observable<IResponse> {

    dto.name = dto.name ? dto.name : '';

    return this.http.post<IResponse>(this.url + '/AddWallet', dto, {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.token)
    });
  }

  deposit(amount: number): Observable<IResponse> {

    this.smdto.sourceWalletTag = this.pickedWallet;
    this.smdto.amount = Math.round(amount * 100);

    return this.http.post<IResponse>(this.url + '/Deposit', this.smdto, {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.token)
    });
  }

  withdrawal(amount: number): Observable<IResponse> {

    this.smdto.sourceWalletTag = this.pickedWallet;
    this.smdto.amount = Math.round(amount * 100);

    return this.http.post<IResponse>(this.url + '/Withdrawal', this.smdto, {
      headers: new HttpHeaders().set('Authorization', 'Bearer ' + this.token)
    });
  }
}
