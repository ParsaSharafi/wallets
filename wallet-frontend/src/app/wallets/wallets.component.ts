import { Component, inject } from '@angular/core';
import { ApiService } from '../api.service';
import { IResponse, ITransaction, IWallet } from '../models';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-wallets',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './wallets.component.html',
  styleUrl: './wallets.component.css'
})
export class WalletsComponent {

  api: ApiService = inject(ApiService);
  wallets: IWallet[] = [];
  transactions: ITransaction[] = [];
  pickedWallet: string = '';
  sub!: Subscription;
  numStr: string = '';
  pos: number = 0;

  ngOnInit(): void {
    this.showWallets();
    this.sub = this.api.refresh$.subscribe(() => this.showWallets());
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  showWallets() {
    this.api.listWallets().subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful) {
          this.wallets = result.data;
          if (this.wallets.length > 0) {
            if (this.api.getPickedWallet() == '') {
              this.pickedWallet = this.wallets[0].tag;
              this.api.setPickedWallet(this.wallets[0].tag);
            }
            this.showTransactions();
          }
        }

        else
          alert(result.data);
      },
      error: (result: any) => {
        alert(result.message);
      }
    });
  }

  pick(tag: string): void {
    this.pickedWallet = tag;
    this.api.setPickedWallet(tag);
    this.showWallets();
  }

  round(num: number): string {
    this.numStr = num.toString();
    this.pos = this.numStr.length - 2;
    if (this.pos <= 0)
      return '0.' + this.numStr;
    return this.numStr.slice(0, this.pos) + '.' + this.numStr.slice(this.pos)
  }

  showTransactions(): void {
    this.api.listTransactions().subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful)
          this.transactions = result.data;

        else
          alert(result.data);
      },
      error: (result: any) => {
        alert(result.message);
      }
    });
  }
}
