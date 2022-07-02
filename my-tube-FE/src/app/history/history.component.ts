import { Component, OnInit } from '@angular/core';
import { UserService } from '../user.service';

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  history: Array<string> = [];

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getHistory();
  }


  getHistory(){
    this.userService.userHistory(this.userService.getUserId()).subscribe(data => {
      this.history = data;
    });
  }
}
