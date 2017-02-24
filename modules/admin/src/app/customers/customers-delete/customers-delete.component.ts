import {Component} from "@angular/core";
import {TranslateService} from "ng2-translate/ng2-translate";
import {Router, ActivatedRoute} from "@angular/router";
import {Location} from "@angular/common";
import {Message} from "primeng/components/common/api";
import {NotificationService} from "../../services/notification-service";
import {CustomersService} from "../customer.service";
import {Observable} from "rxjs";
import {Response} from "@angular/http";

@Component({
    selector: 'customers-delete',
    templateUrl: './../../shared/templates/delete.component.html',
    styleUrls: ['./../../shared/styles/delete.component.scss'],
    providers: []
})

export class CustomersDeleteComponent {
    public id: number;
    public msgs: Message[] = [];

    constructor(public translate: TranslateService,
                public customersService: CustomersService,
                public router: Router,
                public route: ActivatedRoute,
                public location: Location,
                public notifications: NotificationService) {
    }

    ngOnInit() {
        this.translate.get('customers.confirmDeleteMsg')
            .subscribe(detail => {
                this.msgs.push({severity: 'warn', detail: detail});
            });

        this.route.params.subscribe((params) => {
            this.id = +params['customerId'];
        });
    }

    onBack() {
        this.location.back();
    }

    deleteResource(): Observable<Response> {
        return Observable.create(obs => {
            this.customersService.deleteResourceById(this.id)
                .subscribe((res) => {
                    this.onBack();
                    this.notifications.createNotification('success', 'SUCCESS', 'customers.successDelete');
                    obs.next(res);
                }, err => {
                    console.error(err);
                    this.notifications.createNotification('error', 'ERROR', 'customers.errorDelete');
                    obs.error(err);
                });
        });
    }
}
