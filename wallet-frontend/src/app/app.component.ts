import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ApiService } from './api.service';
import { FormsModule } from '@angular/forms';
import { WalletsComponent } from './wallets/wallets.component';
import { IUser, IResponse } from './models';
import { CurrenciesComponent } from './currencies/currencies.component';
import { PanelComponent } from './panel/panel.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, FormsModule, WalletsComponent, CurrenciesComponent, PanelComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {

  api: ApiService = inject(ApiService);
  loggedIn: boolean = false;
  user: IUser = {};

  ngOnInit(): void {
      this.loggedIn = this.api.isLoggedIn();
  }

  logIn(user: IUser): void {
    this.api.logIn(user).subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful) {
          this.api.setToken(result.data);
          this.loggedIn = true;
        }
        else
          alert(result.data);
      },
      error: (result: any) => {
        alert("PASSWORD IS INCORRECT");
      }
    });
  }

  logOut(): void {
    this.api.setToken('');
    window.location.reload();
  }

  signUp(user: IUser): void {
    this.api.signUp(user).subscribe({
      next: (result: IResponse) => {
        if (result.isSuccessful) {
          this.api.setToken(result.data);
          this.loggedIn = true;
        }
        else
          alert(result.data);
      },
      error: (result: any) => {
        alert(result.message);
      }
    });
  }
}
