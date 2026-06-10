import { Component, inject } from '@angular/core';
import { ICurrency } from '../models';
import { Observable, Subscription, timer } from 'rxjs';
import { ApiService } from '../api.service';

@Component({
  selector: 'app-currencies',
  standalone: true,
  imports: [],
  templateUrl: './currencies.component.html',
  styleUrl: './currencies.component.css'
})
export class CurrenciesComponent {

  api: ApiService = inject(ApiService);
  sub!: Subscription;
  currencies: ICurrency[] = [];
  subscription: Subscription = new Subscription;
  everyTenSeconds: Observable<number> = timer(0, 10000);

  ngOnInit(): void {
    this.subscription = this.everyTenSeconds.subscribe(() => { this.api.updateCurrencies(); })
    this.sub = this.api.currencies$.subscribe((c: ICurrency[]) => this.currencies = c);
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
    this.subscription.unsubscribe();
  }
}
