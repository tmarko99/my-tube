import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { UserService } from '../user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  isAuthenticated: boolean = false;

  constructor(private oidcSecurityService: OidcSecurityService,
    private userService: UserService, private router: Router) {
      this.userService.registerUser();
      this.router.navigateByUrl['/featured']
     }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe((auth) => {
      this.isAuthenticated = auth;
    });
  }

  login() {
    this.oidcSecurityService.authorize();
  }

  logOff() {
    this.oidcSecurityService.logoffAndRevokeTokens();
    this.oidcSecurityService.logoffLocal();
  }

}
