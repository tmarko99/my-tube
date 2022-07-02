import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';

@Component({
  selector: 'app-subscriptions',
  templateUrl: './subscriptions.component.html',
  styleUrls: ['./subscriptions.component.css']
})
export class SubscriptionsComponent implements OnInit {

  subscriptionHistory: string[] = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getSubscriptionHistory();
  }

  getSubscriptionHistory(){
    this.userService.subscriptionHistory(this.userService.getUserId()).subscribe(data => {
      this.subscriptionHistory = data;
    })
  }

}
