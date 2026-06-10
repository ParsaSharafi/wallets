import { Component, inject } from '@angular/core';
import { ApiService } from '../api.service';
import { IAddWallet, ICurrency, IFees, IResponse, ISendMoney } from '../models';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-panel',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './panel.component.html',
  styleUrl: './panel.component.css'
})
export class PanelComponent {

  api: ApiService = inject(ApiService);
  errors: Map<string, string> = new Map();
  smdto: ISendMoney = {};
  awdto: IAddWallet = {};
  currencies: ICurrency[] = [];
  sub!: Subscription;
  fees: IFees = { transferFee: 0, exchangeFee: 0 };
  dAmount: number = 0;
  wAmount: number = 0;
  fee: number = 0;
  payable: number = 0;
  pw: string = '';

  ngOnInit(): void {
    this.api.getFees().subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful)
          this.fees = result.data;

        else
          alert(result.data);
      },
      error: (result: any) => {
        alert(result.message);
      }
    });

    this.sub = this.api.currencies$.subscribe((c: ICurrency[]) => this.currencies = c);
  }

  sendMoney(dto: ISendMoney): void {
    this.api.sendMoney(dto).subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful)
          this.api.refresh();

        else
          alert(result.data);
      },
      error: (result: any) => {
        alert(result.message);
      }
    });
    this.smdto.amount = 0;
    this.fee = 0;
    this.payable = 0;
  }

  calculate(): void {

    this.fee = 0;
    this.payable = 0;
    this.pw = this.api.getPickedWallet();

    if (!this.smdto.amount || !this.smdto.destinationWalletTag || !this.fees.transferFee || !this.fees.exchangeFee)
      return;

    if (this.smdto.destinationWalletTag.substring(0, 4) == this.pw.substring(0, 4)) {
      if (this.smdto.destinationUsername)
        this.fee = this.fees.transferFee * this.smdto.amount;
      this.payable = this.smdto.amount - this.fee;
    } else {
      if (!this.smdto.destinationUsername)
        this.fee = this.fees.exchangeFee * this.smdto.amount;
      else
        this.fee = (this.fees.exchangeFee + this.fees.transferFee) * this.smdto.amount;
      this.payable = this.smdto.amount - this.fee;
      if (this.pw.substring(0, 3) != 'USD') {
        const cCode = this.currencies.find(c => c.code === this.pw.substring(0, 3));
        this.payable = cCode ? this.payable * cCode?.rate : this.payable;
      }
      if (this.smdto.destinationWalletTag.substring(0, 3) != 'USD') {
        const cCode = this.currencies.find(c => c.code == this.smdto.destinationWalletTag?.substring(0, 3));
        this.payable = cCode ? this.payable / cCode?.rate : this.payable;
      }
    }
  }

  addWallet(dto: IAddWallet): void {
    this.api.addWallet(dto).subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful)
          this.api.refresh();

        else
          alert(result.data);
      },
      error: (result: any) => {
        alert(result.message);
      }
    });
  }

  deposit(amount: number): void {
    this.api.deposit(amount).subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful)
          this.api.refresh();

        else
          alert(result.data);
      },
      error: (result: any) => {
        alert(result.message);
      }
    });
  }

  withdrawal(amount: number): void {
    this.api.withdrawal(amount).subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful)
          this.api.refresh();

        else
          alert(result.data);
      },
      error: (result: any) => {
        alert(result.message);
      }
    });
  }
}
