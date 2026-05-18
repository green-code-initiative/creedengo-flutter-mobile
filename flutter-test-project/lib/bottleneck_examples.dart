import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class BottleneckExamples {
  
  // GCI3: Collection size in loop - VIOLATION
  void badLoop(List<String> items) {
    for (int i = 0; i < items.length; i++) { // VIOLATION
      print(items[i]);
    }
  }
  
  // GCI535: Network requests in loop - VIOLATION
  void badNetworkLoop(List<String> urls) {
    for (var url in urls) {
      // VIOLATION: Requête réseau dans une boucle
      http.get(Uri.parse(url));
    }
  }
}
