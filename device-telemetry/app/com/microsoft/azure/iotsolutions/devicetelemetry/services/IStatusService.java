// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.devicetelemetry.services;

import com.google.inject.ImplementedBy;
import com.microsoft.azure.iotsolutions.devicetelemetry.services.models.StatusServiceModel;

@ImplementedBy(StatusService.class)
public interface IStatusService {
    StatusServiceModel getStatus(boolean authRequired);
}
