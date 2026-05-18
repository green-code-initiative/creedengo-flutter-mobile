import 'package:flutter/material.dart';
import 'dart:async';

// GCI79: State sans dispose() - VIOLATION
class BadStateWidget extends StatefulWidget {
  @override
  _BadStateWidgetState createState() => _BadStateWidgetState();
}

class _BadStateWidgetState extends State<BadStateWidget> {
  Timer? timer;
  
  @override
  void initState() {
    super.initState();
    timer = Timer.periodic(Duration(seconds: 1), (t) => print('tick'));
    // VIOLATION: Pas de dispose() pour libérer le timer
  }
  
  @override
  Widget build(BuildContext context) {
    return Container();
  }
}

// GCI530: Torch usage - VIOLATION
class TorchWidget extends StatelessWidget {
  void enableTorch() async {
    // VIOLATION: Utilisation de la torche
    await controller.setTorchMode(true);
  }
  
  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
