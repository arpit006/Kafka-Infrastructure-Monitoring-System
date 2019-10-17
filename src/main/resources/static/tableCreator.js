function buildHtmlTable(selector) {
    $.get("http://localhost:8365/monitor",function(data,status){
        for (var i = 0; i < data.length; i++) {
            var div$ = $('<div>');
            var header$ = $('<h2 align="center"/>');
            header$.append("Consumer Group Name: "+data[i].consumerGroupName);
            $(div$).append(header$);
            for(var topicCount = 0;topicCount < data[i].topic.length;topicCount++){
                var topicHeader$ = $('<h3 align="center">');
                var topicLag$ = $('<h4 align="center">');
                var status$ = $('<h4 align="center">');
                var speed$ = $('<h4 align="center">');
                var topicData = data[i].topic[topicCount];
                var totalConsumerSpeed = data[i].totalConsumerSpeed;
                var totalProducerSpeed = data[i].totalProducerSpeed;
                var producerStatus = data[i].isProducerActive;
                var consumerStatus = data[i].isConsumerActive;
                topicHeader$.append("Topic Name: "+topicData.topicName);
                topicLag$.append("Topic Total Lag: "+topicData.totalLag);
                status$.append("Producer Status: "+ producerStatus+ "                       Consumer Status: " + consumerStatus);
                speed$.append("Total Producer Speed: "+ totalProducerSpeed + "              Total Consumer Speed: " + totalConsumerSpeed);
                $(div$).append(topicHeader$);
                $(div$).append(topicLag$);
                $(div$).append(status$);
                $(div$).append(speed$);
                $(selector).append(div$);
                var table$ = $('<table border="1" align="center" width="80%"/>');
                $(selector).append(table$);
                table$.append($('<th/>').html('<b>Partition</b>'));
                table$.append($('<th/>').html('<b>Previous Partition Offset</b>'));
                table$.append($('<th/>').html('<b>Latest Partition Offset</b>'));
                table$.append($('<th/>').html('<b>Producer Write Speed </b>'));
                table$.append($('<th/>').html('<b>Producer Active </b>'));
                table$.append($('<th/>').html('<b>Producer Average Speed </b>'));
                table$.append($('<th/>').html('<b>Previous Consumer Offset</b>'));
                table$.append($('<th/>').html('<b>Current Consumer Offset</b>'));
                table$.append($('<th/>').html('<b>Consumer Read Speed</b>'));
                table$.append($('<th/>').html('<b>Consumer Active</b>'));
                table$.append($('<th/>').html('<b>Consumer Average Speed</b>'));
                table$.append($('<th/>').html('<b>Partition Lag</b>'));

                for (var partitionCount = 0; partitionCount < topicData.partition.length; partitionCount++) {
                    var row$ = $('<tr/>');
                    var partitionData = topicData.partition[partitionCount];

                    var partitionId = partitionData.partition;
                    var prevPartitionOffset = partitionData.previousPartitionOffset;
                    var latestOffset = partitionData.latestPartitionOffset;
                    var prevConsumerOffset = partitionData.previousConsumerOffset;
                    var consumerOffset = partitionData.latestConsumerOffset;
                    var lag = partitionData.lag;
                    var lpwt = partitionData.lastProducerWriteTime;
                    var lcrt = partitionData.lastConsumerReadTime;
                    var writeSpeed = partitionData.writeSpeed;
                    var readSpeed = partitionData.readSpeed;
                    var producerStatus = partitionData.isProducerActive;
                    var consumerStatus = partitionData.isConsumerActive;
                    var avgProdSpeed =partitionData.averageWriteSpeed;
                    var avgConsSpeed=partitionData.averageReadSpeed;

                    row$.append($('<td width="10%" align="center"/>').html(partitionId));
                    row$.append($('<td width="10%" align="center"/>').html(prevPartitionOffset));
                    row$.append($('<td width="10%" align="center"/>').html(latestOffset));
                    row$.append($('<td width="5%" align="center"/>').html(writeSpeed));
                    row$.append($('<td width="5%" align="center"/>').html(producerStatus));
                    row$.append($('<td width="10%" align="center"/>').html(avgProdSpeed));
                    row$.append($('<td width="10%" align="center"/>').html(prevConsumerOffset));
                    row$.append($('<td width="10%" align="center"/>').html(consumerOffset));
                    row$.append($('<td width="5%" align="center"/>').html(readSpeed));
                    row$.append($('<td width="5%" align="center"/>').html(consumerStatus));
                    row$.append($('<td width="10%" align="center"/>').html(avgConsSpeed));
                    row$.append($('<td width="10%" align="center"/>').html(lag));
                    $(table$).append(row$);
                }

            }

        }
    });
}
function addAllColumnHeaders(myList, selector) {
    var columnSet = [];
    var headerTr$ = $('<tr/>');

    for (var i = 0; i < myList.length; i++) {
        var rowHash = myList[i];
        for (var key in rowHash) {
            if ($.inArray(key, columnSet) == -1) {
                columnSet.push(key);
                headerTr$.append($('<th/>').html(key));
            }
        }
    }
    $(selector).append(headerTr$);
    return columnSet;
}